# TODO 각자 레포지토리 이름, Application 이름에 맞게 수정
PROJECT_ROOT="/home/ec2-user/spring-docker-aws-deploy-java17"
cp $PROJECT_ROOT/build/libs/tricountapi-0.0.1-SNAPSHOT.jar $PROJECT_ROOT/
docker build -t tricount-api -f "$PROJECT_ROOT/Dockerfile" .
docker run -d -p 8080:8080 tricount-api