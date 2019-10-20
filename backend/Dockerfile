FROM node:10.16.3-alpine

RUN apk add --no-cache build-base yarn python

WORKDIR /app
COPY . .

RUN yarn
RUN mv -v ./artifacts/* ./node_modules/@oceanprotocol/keeper-contracts/artifacts

ENTRYPOINT ["npm", "start"]
