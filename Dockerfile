# 기본 이미지를 설정합니다. openjdk 이미지와 크롬 브라우저를 포함한 베이스 이미지를 사용합니다.
FROM amd64/amazoncorretto:17

# 작업 디렉토리를 설정합니다.
WORKDIR /app

# 스프링 어플리케이션 JAR 파일을 컨테이너로 복사합니다.
COPY ./build/libs/notify-crawler-0.0.1-SNAPSHOT.jar /app/notify-crawler.jar

# 크롬 브라우저와 크롬 드라이버를 설치합니다.
RUN apt-get update && \
    apt-get install -y wget gnupg unzip && \
    wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list' && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    CHROME_VERSION=$(google-chrome --version | awk '{print $3}') && \
    wget -q "https://chromedriver.storage.googleapis.com/$CHROME_VERSION/chromedriver_linux64.zip" -O /tmp/chromedriver.zip && \
    unzip /tmp/chromedriver.zip -d /usr/local/bin/ && \
    rm /tmp/chromedriver.zip && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 주키퍼와 카프카를 설치하고 실행하는 스크립트를 추가합니다.
COPY install_kafka_zookeeper.sh /app/install_kafka_zookeeper.sh
RUN chmod +x /app/install_kafka_zookeeper.sh
RUN /app/install_kafka_zookeeper.sh

# 환경 변수를 설정합니다.
ENV CHROME_DRIVER_PATH=/usr/local/bin/chromedriver
ENV SPRING_PROFILES_ACTIVE=prod

# 포트를 엽니다.
EXPOSE 8082
EXPOSE 2181
EXPOSE 9092

# 어플리케이션을 실행합니다.
ENTRYPOINT ["java", "-Dwebdriver.chrome.driver=/usr/local/bin/chromedriver", "-jar", "/app/notify-crawler.jar"]
