@echo off
echo GiGA Genie service Batch File start!

rem TODO change
rem S3 Path
set s3Path=s3://etri-system-light/web/
rem Vue file path
set distPath=./dist/

rem echo Clear s3 directory
rem clear Directory
rem aws s3 rm %s3Path% --recursive

echo npm run build
call npm run build


rem If you want to configure in here, put your keys
rem echo Configure aws cli
rem aws configure set AWS_ACCESS_KEY_ID <your acess key id>
rem aws configure set AWS_SECRET_ACCESS_KEY <your secret access key here>
rem aws configure set default.region eu-west-1

echo Upload static web files
call aws s3 sync %distPath% %s3Path%
