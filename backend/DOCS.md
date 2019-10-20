# docs

###### Download file from minio
`curl -i http://157.230.28.206:9050/default/file.txt`

###### Create asset by our API
`curl -i -X POST http://127.0.0.1:7456 -F 'file=@/Users/lluuvr/dev/playground/file.txt' -F 'meta={"name": "ue", "author": "qw", "description": "llo"}'`

###### Start our API
`BARGE_IP=157.230.28.206 API_PORT=7456 MINIO_ENDPOINT=157.230.28.206 MINIO_PORT=9050 MINIO_ACCESS_KEY=Q3AM3UQ867SPQQA43P2F MINIO_SECRET_KEY=zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG npm start`

###### Envs
```shell script
ENV BARGE_IP=157.230.28.206
ENV API_PORT=7456
ENV MINIO_ENDPOINT=157.230.28.206
ENV MINIO_PORT=9050
ENV MINIO_ACCESS_KEY=Q3AM3UQ867SPQQA43P2F
ENV MINIO_SECRET_KEY=zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG
```

###### Run docker container with our API
- Build \
`docker build -t team40 -f ./Dockerfile .`
- Run \
`docker run -e BARGE_IP=157.230.28.206 -e API_PORT=7456 -e MINIO_ENDPOINT=157.230.28.206 -e MINIO_PORT=9050 -e MINIO_ACCESS_KEY=Q3AM3UQ867SPQQA43P2F -e MINIO_SECRET_KEY=zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG --rm -p 7456:7456 team40`
