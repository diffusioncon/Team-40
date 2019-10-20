"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
exports.__esModule = true;
var ocean = require("@oceanprotocol/squid");
var express = require("express");
var minio = require("minio");
var multer = require("multer");
var morgan = require("morgan");
var asset_1 = require("./asset");
var crypto = require("crypto");
var API_PORT = process.env.API_PORT;
var BARGE_IP = process.env.BARGE_IP;
var MINIO_ENDPOINT = process.env.MINIO_ENDPOINT;
var MINIO_PORT = process.env.MINIO_PORT;
var MINIO_ACCESS_KEY = process.env.MINIO_ACCESS_KEY;
var MINIO_SECRET_KEY = process.env.MINIO_SECRET_KEY;
var HASH_ALGORITHM = "MD5";
var BUCKET_NAME = "default";
var multerStorage = multer.memoryStorage();
var uploader = multer({ storage: multerStorage });
var app = express();
var minioClient = new minio.Client({
    endPoint: MINIO_ENDPOINT,
    port: +MINIO_PORT,
    useSSL: false,
    accessKey: MINIO_ACCESS_KEY,
    secretKey: MINIO_SECRET_KEY
});
var oceanClient = null;
var createBucketIfNotExists = function () { return __awaiter(void 0, void 0, void 0, function () {
    var policy, e_1;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                _a.trys.push([0, 5, , 6]);
                return [4 /*yield*/, minioClient.bucketExists(BUCKET_NAME)];
            case 1:
                if (!!(_a.sent())) return [3 /*break*/, 4];
                return [4 /*yield*/, minioClient.makeBucket(BUCKET_NAME, "eu-central-1")];
            case 2:
                _a.sent();
                policy = JSON.stringify({
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Sid": "AddPerm",
                            "Effect": "Allow",
                            "Principal": "*",
                            "Action": ["s3:GetObject"],
                            "Resource": ["arn:aws:s3:::" + BUCKET_NAME + "/*"]
                        }
                    ]
                });
                return [4 /*yield*/, minioClient.setBucketPolicy(BUCKET_NAME, policy)];
            case 3:
                _a.sent();
                _a.label = 4;
            case 4: return [3 /*break*/, 6];
            case 5:
                e_1 = _a.sent();
                console.error(e_1);
                return [3 /*break*/, 6];
            case 6: return [2 /*return*/];
        }
    });
}); };
(function () { return __awaiter(void 0, void 0, void 0, function () {
    var _a, _b, e_2;
    return __generator(this, function (_c) {
        switch (_c.label) {
            case 0:
                _c.trys.push([0, 4, , 5]);
                return [4 /*yield*/, createBucketIfNotExists()];
            case 1:
                _c.sent();
                return [4 /*yield*/, ocean.Ocean.getInstance({
                        nodeUri: "http://" + BARGE_IP + ":8545",
                        aquariusUri: "http://" + BARGE_IP + ":5000",
                        brizoUri: "http://" + BARGE_IP + ":8030",
                        brizoAddress: "0x00bd138abd70e2f00903268f3db08f2d25677c9e",
                        parityUri: "http://" + BARGE_IP + ":9545",
                        secretStoreUri: "http://" + BARGE_IP + ":12001"
                    })];
            case 2:
                oceanClient = _c.sent();
                _b = (_a = console).log;
                return [4 /*yield*/, oceanClient.assets.search("ue")];
            case 3:
                _b.apply(_a, [_c.sent()]);
                return [3 /*break*/, 5];
            case 4:
                e_2 = _c.sent();
                console.error(e_2);
                return [3 /*break*/, 5];
            case 5: return [2 /*return*/];
        }
    });
}); })();
var prepareAsset = function (file, meta, checksum) {
    asset_1["default"].base.name = meta.name;
    asset_1["default"].base.author = meta.author;
    asset_1["default"].base.description = meta.description;
    asset_1["default"].base.files = [{
            index: 0,
            contentType: file.mimetype,
            checksum: checksum,
            checksumType: HASH_ALGORITHM,
            contentLength: file.size,
            encoding: file.encoding,
            url: "http://" + MINIO_ENDPOINT + ":" + MINIO_PORT + "/" + BUCKET_NAME + "/" + file.originalname,
            compression: ""
        }];
    asset_1["default"].base.price = "7";
    asset_1["default"].base.dateCreated = new Date().toISOString().split(".")[0] + "Z";
    return asset_1["default"];
};
app.use(morgan("common"));
app.post("/", uploader.any(), function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var meta, file, checksum, asset_2, accounts, acc, ddo, e_3;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                _a.trys.push([0, 4, , 5]);
                meta = JSON.parse(req.body.meta);
                file = req.files[0];
                return [4 /*yield*/, minioClient.putObject(BUCKET_NAME, file.originalname, file.buffer)];
            case 1:
                _a.sent();
                checksum = crypto.createHash(HASH_ALGORITHM).update(file.buffer).digest("hex");
                asset_2 = prepareAsset(file, meta, checksum);
                return [4 /*yield*/, oceanClient.accounts.list()];
            case 2:
                accounts = _a.sent();
                acc = accounts[0];
                console.log(asset_2);
                return [4 /*yield*/, oceanClient.assets.create(asset_2, acc)];
            case 3:
                ddo = _a.sent();
                console.log(ddo.id);
                res.sendStatus(200);
                return [3 /*break*/, 5];
            case 4:
                e_3 = _a.sent();
                console.error(e_3);
                res.sendStatus(500);
                return [3 /*break*/, 5];
            case 5: return [2 /*return*/];
        }
    });
}); });
app.listen(API_PORT, function () { return console.log("app listening on port " + API_PORT); });
