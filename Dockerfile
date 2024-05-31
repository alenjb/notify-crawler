# 기본 이미지를 설정합니다.
FROM ubuntu:22.04

# Java 설치
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk wget gnupg unzip && \
    rm -rf /var/lib/apt/lists/*

# 작업 디렉토리를 설정합니다.
WORKDIR /app

# 스프링 어플리케이션 JAR 파일을 컨테이너로 복사합니다.
COPY ./build/libs/notify-crawler-0.0.1-SNAPSHOT.jar /app/notify-crawler.jar

# 크롬 브라우저와 크롬 드라이버를 설치합니다.
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list' && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    wget -q "https://chromedriver.storage.googleapis.com/114.0.5735.90/chromedriver_linux64.zip" -O /tmp/chromedriver.zip && \
    unzip /tmp/chromedriver.zip -d /usr/local/bin/ && \
    rm /tmp/chromedriver.zip && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 환경 변수를 설정합니다.
ENV CHROME_DRIVER_PATH=/usr/local/bin/chromedriver
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$PATH:$JAVA_HOME/bin

# 포트를 엽니다.
EXPOSE 8082

# 어플리케이션을 실행합니다.
ENTRYPOINT ["java", "-Dwebdriver.chrome.driver=/usr/local/bin/chromedriver", "-jar", "/app/notify-crawler.jar"]
