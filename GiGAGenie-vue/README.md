# GiGAGenie-vue

## System light 2.0

### GiGAGenie service

- Modify Config file
  - src/vuex/modules/common.js.example -> src/vuex/modules/common.js
- Vue test
  ```
    # install dependencies
    npm install

    # start dev pages
    npm start
  ```
- Build for static web files
  - 아래의 명령어를 실행할 경우 프로젝트의 dist 폴더에 저장됨.
  ```
    npm run build
  ```
  - HTTP server를 통해 접근 가능
  - html 파일을 file:// 를 통해 테스트하는 것은 불가능
- S3 버킷 정적 호스팅 (S3 bucket - static web hosting)
  - [Link](https://docs.aws.amazon.com/ko_kr/AmazonS3/latest/user-guide/static-website-hosting.html) 참고
  - [AWS CLI](https://docs.aws.amazon.com/ko_kr/cli/latest/userguide/cli-chap-welcome.html)를 활용하여 S3에 업로드할 수 있음
    - [Link](https://docs.aws.amazon.com/ko_kr/cli/latest/userguide/using-s3-commands.html) 참고
    - 배치 파일 활용 방법
      - 프로젝트 내 s3_upload.bat 파일로 업로드 가능
    - 쉘 스크립트 파일 활용 방법
      - 프로젝트 내 s3_upload.sh 파일로 업로드 가능
      - 커맨드에서 아래의 명령어를 실행하여 해당 파일을 실행 가능하게 만든 후 실행
      ```
        sudo chmod +x s3_upload.sh
      ```
- Build static web files and Upload AWS S3 for AWS CloudFront
  - 프로젝트 빌드 후에 S3에 업로드한 후 CloudFront에 연결하여 이용 가능



--------------------------------------------------

> A Vue.js project

#### Build Setup

```
# install dependencies
npm install

# serve with hot reload at localhost:8080
npm run dev

# build for production with minification
npm run build

# build for production and view the bundle analyzer report
npm run build --report

# run unit tests
npm run unit

# run e2e tests
npm run e2e

# run all tests
npm test
```

For a detailed explanation on how things work, check out the [guide](http://vuejs-templates.github.io/webpack/) and [docs for vue-loader](http://vuejs.github.io/vue-loader).
