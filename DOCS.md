# docs

###### Download file from minio
`curl -i http://157.230.28.206:9050/default/file.txt`

###### Create asset by our API
`curl -i -X POST http://127.0.0.1:7456 -F 'file=@/Users/lluuvr/dev/playground/file.txt' -F 'meta={"name": "ue", "author": "qw", "description": "llo"}'`

###### Start our API
`BARGE_IP=157.230.28.206 API_PORT=7456 MINIO_ENDPOINT=157.230.28.206 MINIO_PORT=9050 MINIO_ACCESS_KEY=Q3AM3UQ867SPQQA43P2F MINIO_SECRET_KEY=zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG npm start`
