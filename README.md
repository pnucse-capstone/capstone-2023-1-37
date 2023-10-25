# 클라우드 기반 원격 DaaS 

2023년 전기 졸업과제 37조 클라우드 기반 원격 DaaS

<br>

## **1. 프로젝트 소개**

### **개요**

- 본 과제는 코로나 19 이후 원격 교육과 재택 근무 필요성이 급증한 현대 사회에서 교육 분야의 비대면 교육 혁신을 위해 **클라우드 기반의 원격 데스크탑 서비스(DaaS, Desktop-as-a-Service)와 여러 학습 관리 기능을 결합한 교육 플랫폼(LMS, Learning Management System)** 을 구축한다. 이를 통해 비대면 교육뿐만 아니라 학습 환경의 품질을 향상시키고 효과적인 학습 관리 서비스를 제공하고자 한다.

<br>

### **내용**
- 언제 어디서든 접근 가능한 원격 데스크탑 환경을 제공한다.
- 클라우드 기반 원격 데스크탑 서비스를 웹 서비스의 형태로 제공하여 별도의 애플리케이션 설치나 복잡한 설정 없이 간편하게 사용할 수 있다.
- Kubernetes를 사용하여 autoscaling을 제공해 트래픽 증가, 장애 상황에 대응하여 안정적이고 중단 없는 서비스를 제공한다.
- 서버의 부하를 분산시키기 위해 가상 환경 이미지 관리 서버, 가상 환경 접속 서버, 웹 서버를 분리하여 마이크로서비스 아키텍처(MSA, MicroService Architecture)를 실현한다.
- 교육 플랫폼을 제공하여 교육자가 강좌를 효과적으로 관리하고 학생들이 학습 자료와 정보에 쉽게 접근할 수 있다.
- 학생은 교육자의 학습 환경과 동일한 환경을 구성하기 위해 압축 파일, 설명서를 통해 직접 환경 설정할 필요 없이 편리하게 교육자와 동일한 학습 환경을 구성할 수 있다.
- 타 사용자의 가상 환경에서 함께 작업할 수 있다.
- harbor와 s3를 사용한 private docker registry를 구축하여 사용자들의 가상 환경인 docker image를 안전하게 관리한다.
- kubernetes로 구축한 서버를 프로메테우스(Prometheus)와 그라파나(Grafana)를 활용하여 모니터링한다. 또한, private docker registry에 사용된 s3를 AWS CloudWatch로 모니터링한다.

<br>

### **개발 환경**

| 분야                        | 사용 기술 및 도구                                     |
|:---------------------------:|------------------------------------------------------|
| **Programming Languages**   | - Java 17 <br> - Python 3.8.10 |
| **Framework**               | - Spring Boot 3.1.2 <br> - Flask 3.0.0 |
| **Template Engine**         | - Thymeleaf 3.1.2 |
| **Build Tool**              | - Gradle 8.2.1 |
| **Cloud**                   | - Docker 24.0.6 <br> - Kubernetes 1.25.8 <br> - Amazon Web Services(AWS) <br> - Naver CloudPlatform |
| **DataBase**                | - MariaDB 10.0 |
| **Registry**                | - Harbor 2.8.2 <br> - AWS S3 |
| **Monitoring**              | - Prometheus 2.20.1 <br> - Grafana 8.5.27 <br> - AWS CloudWatch |
| **Version Control**         | - Git 2.34.1 |  

<br>

## **2. 팀소개**

|**이름**|**Github/Email**|**역할**|
|---|---|---|
| **박소현** | • https://github.com/sososo0 <br> • sh0000@pusan.ac.kr |• 전체 구성 설계 및 구축 <br> • 클라우드(AWS, Naver Cloud) 환경 구축 및 멀티 클라우드 적용 <br> • Private Docker Registry 설계 및 구축 <br> • Web Desktop Manager 설계 및 구축 <br> • Kubernetes & Web Desktop Image Manger 설계 및 구축 <br> • 가상환경 API 구현(Flask) <br> • 서비스 배포|
| **김기해** | • https://github.com/xcelxlorx <br> • xcelxlorx@gmail.com |• UI 설계 <br>• 강좌, 게시판, 관리자, 사용자 API 구현(SpringBoot) <br> • 모니터링 구현  <br> • 강좌, 게시판, 관리자, 사용자 페이지 구현|
| **김수현** | • https://github.com/suuding <br> • tngus4789@naver.com |• 기능 정의서 작성 및 DB 설계 <br> • 로그인, 회원가입, 가상환경 API 구현(SpringBoot) <br> • 가상환경 API 구현, Container Image 생성 및 관리(Flask) <br> • 웹 서버와 가상환경 서버 API 통신 구현 <br> • 로그인, 회원가입, 가상환경 페이지 구현 |

<br>

## **3. 시스템 구성도**

### **전체 구성도**

<img src="https://github.com/pnucse-capstone/capstone-2023-1-37/assets/96944649/ace38ab5-1619-4afb-b47c-e5b964c55ca4" width="700"/>

- **멀티 클라우드**
  - 해당 서비스는 멀티 클라우드 아키텍처로 구성되어 있으며, 클라우드 서비스 공급자는 AWS와 Naver CloudPlatform이다.
- **AWS**
  - Web Server에서 웹 애플리케이션 호스팅 및 웹 사이트를 제공하기 위해 AWS의 EC2를 사용한다.
  - AWS의 S3를 사용하여 Private Docker Registry를 구축했다.
  - AWS CloudWatch를 사용하여 S3 리소스를 모니터링한다.
- **Naver CloudPlatform**
  - Kubernetes 관리 및 Web Desktop Image를 관리하는 역할을 수행한다.
  - k8s 기반으로 구축된 Web Desktop Manager에서 가상 환경을 관리하고 사용자들의 리소스 사용량에 따라 k8s 환경을 scaling한다.
  - k8s 리소스 사용량을 보기 위해 모니터링한다.

<br>

### **가상환경 관리 서버 구성도**

<img src="https://github.com/pnucse-capstone/capstone-2023-1-37/assets/96944649/0b90616f-6ee0-49cc-9947-e9ab8bdabddf" width="700">

- 가상환경 관리 서버는 웹 서버의 요청에 따라 가상환경 생성, 실행, 접속, 삭제를 수행하고 사용자의 docker image를 안전하게 저장한다.
- **Kubernetes & Web Desktop Image Manager(Flask Server)**
  - 사용자 요청을 바탕으로 k8s에 명령을 내리고 docker image를 관리한다.
- **Web Desktop Manager(Kubernetes Server)**
  - 클라우드 환경에서 가상환경(kasm container)을 관리한다.
- **Private Docker Registry(Harbor)**
  - docker image를 저장하는 private 저장소이다.

<br>

### **웹 서버 구성도**

<img src="https://github.com/pnucse-capstone/capstone-2023-1-37/assets/96944649/e37c90e0-dcf2-44da-8537-34d572d9b3ad" width="700"/>

- AWS의 EC2 인스턴스를 활용하여 웹 애플리케이션 호스팅과 웹 사이트를 제공한다.

<br>

### **Private Docker Registry 구성도**

<img src="https://github.com/pnucse-capstone/capstone-2023-1-37/assets/96944649/adf36c37-ca05-4fa0-af6f-71d6a26efdb2" width="700"/>

- 오픈 소스 컨테이너 레지스트리 Harbor와 AWS 클라우드 스토리지 서비스 S3를 결합하여 구축한다. Harbor는 Ec2 인스턴스 위에서 Multi Container 형태로 실행된다.
- 트래픽 분산과 서비스 무중단을 위해 Auto Scaling Group으로 구성하고 Load Balancer를 연동한다.
- Harbor와 S3를 연동하여 데이터 복제 및 백업을 지원한다.
- 사용자는 CLI와 Web UI로 접근할 수 있다.

<br>

### **관리자 서비스 구성도**

<img src="https://github.com/pnucse-capstone/capstone-2023-1-37/assets/96944649/36863e2e-a933-4a71-8c65-4fa2d869b297" width="700"/>

- 관리자는 Web Server, 사용자의 Web Desktop, Kubernetes & Web Desktop Image Manager, private docker registry에 접근할 수 있다.

<br>

### **k8s 모니터링 구성도**

<img src="https://github.com/pnucse-capstone/capstone-2023-1-37/assets/96944649/41f7d3a8-8e94-4ade-8458-517d7fdfb428" width="700"/>

- Grafana를 통해 k8s를 모니터링한다.

<br>

### **s3 모니터링 구성도**

<img src="https://github.com/pnucse-capstone/capstone-2023-1-37/assets/96944649/c08197e0-6c48-4818-8282-707f5d309ad2" width="700"/>

- AWS CloudWatch를 통해 s3를 모니터링한다.

<br>

## **4. 소개 및 시연 영상**

### **접속**

- [Web Service 접속 URL](www.p2kcloud.com)
- [Private Docker Registry 접속 URL](registry.p2kcloud.com)


### **소개**

|**이름**|**내용**|
|:---:|:---:|
| **메인 페이지** | <img src="https://github.com/pnucse-capstone/capstone-2023-1-37/assets/96944649/582af3f5-a7e7-4205-a16c-9dd87259f86b" width="550"/> |
| **자신의 가상환경 목록 화면** | <img src="https://github.com/pnucse-capstone/capstone-2023-1-37/assets/96944649/bc021408-0776-4581-8a73-e692dbbcbfac" width="550"/> |
| **가상환경 접속 화면** | <img src="https://github.com/pnucse-capstone/capstone-2023-1-37/assets/96944649/bc810eb8-ce5c-4e91-9cfe-17ed1a1a1f29" width="550"/> |
| **강좌 게시판 화면** | <img src="https://github.com/pnucse-capstone/capstone-2023-1-37/assets/96944649/eb6febe5-cd71-44c4-9e04-44a54cb1eb4d" width="550"/> | 
| **교육자의 가상환경 화면** | <img src="https://github.com/pnucse-capstone/capstone-2023-1-37/assets/96944649/08b5bb08-3762-4a5b-ba58-68e83eac944b" width="550"/> | 
| **관리자의 모니터링 화면** | <img src="https://github.com/pnucse-capstone/capstone-2023-1-37/assets/96944649/a78dada9-b6ee-4332-b7e3-5799a050db6e" width="550"/> | 

<br>

### **시연 영상**

[![부산대학교 정보컴퓨터공학부 소개](http://img.youtube.com/vi/zh_gQ_lmLqE/0.jpg)](https://youtu.be/zh_gQ_lmLqE)

<br>

## **5. 설치 및 사용법**

**Use [PNU-P2K](https://github.com/PNU-P2K) for Deploy**
- [SpringBoot Application](https://github.com/PNU-P2K/webpage.git)
- [Flask Application](https://github.com/PNU-P2K/vm-server.git)

<br>

### Set Env

Copy example yml file to set env

```
cp docker-compose.yml.example docker-compose.yml
cp application-aws.yml.example application-aws.yml
cp application-mariaDB.yml.example application-mariaDB.yml
```
- **docker-compose.yml**
```
version: '3'
services:
  db:
    image: mariadb:10
    container_name: mariadb
    ports:
      - 3307:3306
    environment:
      MARIADB_DATABASE: 'p2k'
      MARIADB_ROOT_PASSWORD: # password
      MARIADB_ROOT_HOST: '%'
      TZ: Asia/Seoul
    volumes:
      - ./mariadb/conf.d:/etc/mysql/conf.d
      - ./mariadb/data:/var/lib/mysql
      - ./mariadb/initdb.d:/docker-entrypoint-initdb.d
    command: ['--character-set-server=utf8mb4', '--collation-server=utf8mb4_unicode_ci', '--bind-address=0.0.0.0']
  application:
    container_name: springboot
    build: .
    ports:
      - 80:8080
    depends_on:
      - db
```
- **application-aws.yml**
```
cloudwatch:
  credentials:
    access-key: # access-key
    secret-key: # secret-key
  s3:
    bucket-name: "p2k"
```
- **application-mariaDB.yml**
```
spring:
  datasource:
    url: jdbc:mariadb://mariadb:3306/p2k?useSSL=false&createDatabaseIfNotExist=true
    driver-class-name: org.mariadb.jdbc.Driver
    username: root
    password: # password
  jpa:
    database-platform: org.hibernate.dialect.MariaDBDialect
    defer-datasource-initialization: true
    hibernate:
      ddl-auto: update
    show-sql: true
    generate-ddl: true
```
### Execution
- **Install Docker in SpringBoot & Flask Application**
```
# Install Docker
sudo apt-get update
sudo apt-get install apt-transport-https ca-certificates curl gnupg-agent software-properties-common
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io

# Check Docker Install
sudo systemctl status docker
```
- **SpringBoot Application**
```
# Repository git clone
git clone https://github.com/PNU-P2K/webpage.git

# Install JDK 17
sudo apt update
sudo apt install openjdk-17-jdk

# Install docker-compose 
sudo curl -L "https://github.com/docker/compose/releases/download/1.24.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

# Check docker-compose install with version 
docker-compose --version 

# Run SpringBoot Application
cd webpage
chmod +x gradlew
./gradlew clean build
docker-compose up -d --build
```
- **Flask Application**
```
# Repository git clone
git clone https://github.com/PNU-P2K/vm-server.git

# Install pip
sudo apt update
sudo apt install python3-pip

# Install Flask
pip install flask

# Install pycrypto
pip install pycrypto

# Download Base Docker Image
docker pull registry.p2kcloud.com/base/1/kasmweb:v1

# Run Flask Application
python3 app_v7.py
```