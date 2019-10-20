import * as ocean from "@oceanprotocol/squid";
import * as express from "express";
import * as minio from "minio";
import * as multer from "multer";
import * as morgan from "morgan";
import asset from "./asset";
import * as crypto from "crypto";

const API_PORT = process.env.API_PORT;
const BARGE_IP = process.env.BARGE_IP;
const MINIO_ENDPOINT = process.env.MINIO_ENDPOINT;
const MINIO_PORT = process.env.MINIO_PORT;
const MINIO_ACCESS_KEY = process.env.MINIO_ACCESS_KEY;
const MINIO_SECRET_KEY = process.env.MINIO_SECRET_KEY;

const HASH_ALGORITHM = "MD5";

const BUCKET_NAME = "default";

const multerStorage = multer.memoryStorage();
const uploader = multer({storage: multerStorage});
const app = express();

const minioClient = new minio.Client({
  endPoint: MINIO_ENDPOINT,
  port: +MINIO_PORT,
  useSSL: false,
  accessKey: MINIO_ACCESS_KEY,
  secretKey: MINIO_SECRET_KEY
});
let oceanClient: ocean.Ocean = null;

const createBucketIfNotExists = async () => {
  try {
    if (!await minioClient.bucketExists(BUCKET_NAME)) {
      await minioClient.makeBucket(BUCKET_NAME, "eu-central-1");

      const policy = JSON.stringify({
        "Version": "2012-10-17",
        "Statement": [
          {
            "Sid": "AddPerm",
            "Effect": "Allow",
            "Principal": "*",
            "Action": ["s3:GetObject"],
            "Resource": [`arn:aws:s3:::${BUCKET_NAME}/*`]
          }
        ]
      });
      await minioClient.setBucketPolicy(BUCKET_NAME, policy);
    }
  } catch (e) {
    console.error(e);
  }
};

(async () => {
  try {
    await createBucketIfNotExists();

    oceanClient = await ocean.Ocean.getInstance({
      nodeUri: `http://${BARGE_IP}:8545`,
      aquariusUri: `http://${BARGE_IP}:5000`,
      brizoUri: `http://${BARGE_IP}:8030`,
      brizoAddress: "0x00bd138abd70e2f00903268f3db08f2d25677c9e",
      parityUri: `http://${BARGE_IP}:9545`,
      secretStoreUri: `http://${BARGE_IP}:12001`
    });

    console.log(await oceanClient.assets.search("ue"));
  } catch (e) {
    console.error(e);
  }
})();

const prepareAsset = (file: Express.Multer.File, meta: any, checksum: string): ocean.MetaData => {
  asset.base.name = meta.name;
  asset.base.author = meta.author;
  asset.base.description = meta.description;
  asset.base.files = [{
    index: 0,
    contentType: file.mimetype,
    checksum: checksum,
    checksumType: HASH_ALGORITHM,
    contentLength: file.size,
    encoding: file.encoding,
    url: `http://${MINIO_ENDPOINT}:${MINIO_PORT}/${BUCKET_NAME}/${file.originalname}`,
    compression: ""
  }];
  asset.base.price = "7";
  asset.base.dateCreated = new Date().toISOString().split(".")[0] + "Z";

  return asset as ocean.MetaData;
};

app.use(morgan("common"));

app.post("/", uploader.any(), async (req, res) => {
    try {
      const meta = JSON.parse(req.body.meta);
      const file = req.files[0];

      await minioClient.putObject(BUCKET_NAME, file.originalname, file.buffer);

      const checksum = crypto.createHash(HASH_ALGORITHM).update(file.buffer).digest("hex");

      const asset = prepareAsset(file, meta, checksum);
      const accounts = await oceanClient.accounts.list();
      const acc = accounts[0];

      console.log(asset);
      const ddo = await oceanClient.assets.create(asset, acc);
      console.log(ddo.id);

      res.sendStatus(200);
    } catch (e) {
      console.error(e);
      res.sendStatus(500);
    }
  }
);

app.listen(API_PORT, () => console.log(`app listening on port ${API_PORT}`));
