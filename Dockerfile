# JDK 17을 포함한 OpenJDK 이미지를 베이스 이미지로 사용
FROM openjdk:17-jdk-slim

# 필수 패키지 설치
RUN apt-get update && apt-get install -y wget curl unzip

# 작업 디렉토리를 설정합니다.
WORKDIR /app

# 스프링 애플리케이션 JAR 파일을 컨테이너로 복사합니다.
COPY ./build/libs/notify-crawler-0.0.1-SNAPSHOT.jar /app/notify-crawler.jar

# 크롬 설치
RUN wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN apt-get install -y ./google-chrome-stable_current_amd64.deb
RUN rm ./google-chrome-stable_current_amd64.deb

# 크롬 버전 확인
RUN google-chrome --version

# 포트를 엽니다.
EXPOSE 8082

# 어플리케이션을 실행합니다.
ENTRYPOINT ["java", "-jar", "/app/notify-crawler.jar"]
