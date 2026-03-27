# 🐳 Docker 이미지 최적화
> 배포 파이프라인의  속도와 이미지 크기를 고려한 4단계 Docker 이미지 최적화 전략을 적용 <br/>
> Jenkins CI/CD 파이프라인으로 단계별 수치 자동 측정

<br />

## 📋 목차

- [👥 멤버](#-멤버)
- [⚙️ 기술 스택](#️-기술-스택)
- [🗂️ 프로젝트 구조](#️-프로젝트-구조)
- [🚀 최적화 전략 — 4단계](#-최적화-전략--4단계)
- [🔧 Jenkins CI/CD 파이프라인](#-jenkins-cicd-파이프라인)
- [🏁 결론](#-결론)
- [‼️ 트러블슈팅](#️-트러블슈팅)

<br />

---

## 👥 멤버

| <img src="https://github.com/caminobelllo.png" width="160px"> | <img src="https://github.com/Sungjun24s.png" width="160px"> | <img src="https://github.com/chaeyuuu.png" width="160px"> |
|:-------------------------------------------------------------:|:-----------------------------------------------------------:|:---------------------------------------------------------:|
|    __서가영<br>[@caminobelllo](https://github.com/caminobelllo)__    |          __박성준<br>[@Sungjun24s](https://github.com/Sungjun24s)__          |      __이채유<br>[@chaeyuuu](https://github.com/chaeyuuu)__      |

<br />



## ⚙️ 기술 스택

### 실행 환경 구성
<img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white"> <img src="https://img.shields.io/badge/linux-FCC624?style=for-the-badge&logo=linux&logoColor=black"> <img src="https://img.shields.io/badge/ngrok-1F1E37?style=for-the-badge&logo=ngrok&logoColor=white">




### 소스 코드
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">


<br />


## 🗂️ 프로젝트 구조
```
docker-optimization-project/
├── Dockerfile                  # 최종 최적화 이미지 (Step 4: 레이어 캐시 최적화)
├── docker/
│   ├── dockerfile.naive        # 최적화 전 베이스라인
│   ├── dockerfile.step01       # Step 1: 베이스 이미지 교체
│   └── dockerfile.step03       # Step 2: 멀티 스테이지 적용
├── .dockerignore               # 빌드 컨텍스트 필터링
└── src/
```


<br />



## 🚀 최적화 전략 — 4단계
> 두 가지 측면에서 최적화를 진행하였습니다. <br/> 1. 이미지 용량 <br/> 2. 속도

<br />

### 📌 최적화 전 (Naive)

빌드 도구, 소스코드, JDK가 전부 최종 이미지에 포함된 상태

```dockerfile
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
RUN ./gradlew build -x test
CMD ["java", "-jar", "build/libs/docker-optimization-0.0.1-SNAPSHOT.jar"]
```

#### 📈 빌드 결과
<img width="2284" height="494" alt="image" src="https://github.com/user-attachments/assets/99e132bf-b196-407c-b0d5-deea824087a9" />
<img width="880" height="81" alt="image" src="https://github.com/user-attachments/assets/37df00c3-2849-4d8c-a18f-2190b48c2bf7" />


#### ➡️ 최적화 전 docker image 크기 : 832MB


<br />


## 📦 이미지 크기 최적화

### 1️⃣ 베이스 이미지 교체
 
기존 jdk를 Alpine 기반으로 교체해 이미지 크기 감소
 
```dockerfile
# Before
FROM eclipse-temurin:17-jdk        # Ubuntu 기반
 
# After
FROM eclipse-temurin:17-jdk-alpine  # Alpine 기반
```

<img width="2250" height="484" alt="image" src="https://github.com/user-attachments/assets/bf6f8ac5-cd1a-48ba-866b-dc0607b8b5a2" />
<img width="887" height="67" alt="image" src="https://github.com/user-attachments/assets/3440a09d-af20-4f83-9dba-faa07d61e986" />


<br />


#### ➡️ 베이스 이미지 교체 전 후 이미지 크기 비교

|  | Before | After |
|--|--------|-------|
| 이미지 크기 | 832MB | 746B |

<br />

### 2️⃣ 멀티스테이지 빌드

빌드 환경과 실행 환경을 분리 → 최종 이미지에 JAR 파일만 포함

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
COPY application.yml src/main/resources/application.yml
RUN chmod +x ./gradlew && ./gradlew build -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
 
```


> 최종 이미지에 Gradle, JDK, 소스코드 미포함


<img width="2234" height="498" alt="image" src="https://github.com/user-attachments/assets/26acc4db-55d6-4de7-a297-88643ff4d062" />
<img width="857" height="66" alt="image" src="https://github.com/user-attachments/assets/720d5378-447f-4c97-a564-4ac861aab851" />

<br />


| | Before | After |
|--|--------|-------|
| 이미지 크기 | 832MB | 247MB |


<br />

## ⚡ 속도 최적화

### 3️⃣ .dockerignore 추가

- `docker build` 실행 시 불필요한 파일을 빌드 컨텍스트에서 제외
- `.dockerignore` 없이 `COPY . .` 사용 시 `.git/` · `build/` 등 전체 전송


```gitignore
.git/
build/
.idea/
*.md
Dockerfile*
docker-compose*
Jenkinsfile
```

<img width="2238" height="476" alt="image" src="https://github.com/user-attachments/assets/81ed1665-4dd0-4ec0-a18b-b43b63721a1e" />


#### ➡️ 멀티 스테이지 빌드를 진행한 경우와 이미지 용량 크기는 동일하나, **docker push 속도 27s 감소**


<br />

### 4️⃣ 레이어 캐시 최적화

Docker는 레이어 변경 시 이하 레이어를 전부 재실행
→ 변경 빈도가 낮은 의존성을 위쪽, 소스코드를 아래쪽 레이어에 배치

```dockerfile
# Before: 소스 변경 시 의존성 전체 재다운로드
COPY . .
RUN ./gradlew build

# After: 의존성 레이어 캐시 유지
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon
COPY src src
RUN ./gradlew bootJar --no-daemon -x test
```

<img width="2238" height="476" alt="image" src="https://github.com/user-attachments/assets/86e5a5bd-a66b-4012-9be6-037237314611" />

#### ➡️ 코드 변경 없이 재빌드를 했을 때, 캐싱되어 있던 기존 레이어를 재사용하므로 **빌드 속도 약 30배 증가 (1m 32s -> 3s)**

<br />


## 🔧 Jenkins CI/CD 파이프라인

### ✔️ 파이프라인 흐름

```
GitHub Push (webhook)
        │
        ▼
┌───────────────────────────────────────────────────┐
│                  Jenkins Pipeline                 │
│                                                   │
│  Stage 1. Checkout    소스코드 체크아웃               │
│  Stage 2. Build JAR   Gradle 빌드 → JAR 생성        │
│  Stage 3. Docker Build  이미지 빌드 + 크기/시간 측정    │
│  Stage 4. Docker Push   Docker Hub push           │
│  Stage 5. Deploy      컨테이너 실행                  │
│  Stage 6. Cleanup     불필요한 이미지 정리             │
└───────────────────────────────────────────────────┘
```



### ✏️ Stage별 상세 동작


#### Stage 2 — Build JAR
```bash
chmod +x gradlew
START=$(date +%s)
./gradlew bootJar --no-daemon -x test
END=$(date +%s)
echo "▶ Gradle 빌드 소요 시간: $((END - START))초"
```
빌드 성공 시 `build/libs/*.jar` 파일 크기 자동 출력

<br />

#### Stage 3 — Docker Build
```bash
START=$(date +%s)
docker build \
    -t ${IMAGE_NAME}:${IMAGE_TAG} \
    -t ${IMAGE_NAME}:latest \
    -f Dockerfile .
END=$(date +%s)
echo "▶ Docker 빌드 소요 시간: $((END - START))초"
 
# 이미지 크기 출력
docker images ${IMAGE_NAME}:${IMAGE_TAG} \
    --format 'table {{.Repository}}\t{{.Tag}}\t{{.Size}}'
```

<br />

#### Stage 4 — Docker Push
```bash
# Jenkins Credentials에 저장된 Docker Hub 계정으로 로그인
echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
docker push ${IMAGE_NAME}:${IMAGE_TAG}
docker push ${IMAGE_NAME}:latest
```

<br />

#### Stage 5 — Deploy
```bash
# 기존 컨테이너 중지 후 새 버전으로 재실행
docker stop ${CONTAINER_NAME} || true
docker rm   ${CONTAINER_NAME} || true
 
docker run -d \
    --name ${CONTAINER_NAME} \
    --restart unless-stopped \
    -p ${APP_PORT}:8080 \
    -e DB_HOST=172.17.0.1 \
    -e DB_USERNAME=${DB_USER} \
    -e DB_PASSWORD=${DB_PASS} \
    ${IMAGE_NAME}:${IMAGE_TAG}
```

> DB 계정 정보는 Jenkins Credentials에 저장 후 주입 (`db-username`, `db-password`)

<br />



## 🏁 결론

### 단계별 측정 결과

> Jenkins 파이프라인에서 자동 측정한 실제 수치


| 단계 | 이미지 크기 | 재빌드 시간 | 개선 포인트 |
|------|-----------|-----------|-----------|
| Naive | 832MB | 2m 39s | 베이스라인 |
| 베이스 이미지 교체 | 746MB | 1m 36s | 크기 감소 |
| 멀티스테이지 빌드 | 247MB | 1m 33s | 크기 감소 |
| .dockerignore | 247MB | 1m 32s *(특이사항 : docker push 시간 : 20s)* | 컨텍스트 정리 |
| 레이어 캐시 최적화 | 247MB | 3s | 재빌드 속도 |

<br />

### 최적화 전/후 핵심 수치

| | Naive | 최종 최적화 | 개선율 |
|--|-------|-----------|------|
| 이미지 크기 | 832MB | 247MB | 70.3%↓ |
| 소스 변경 시 재빌드 | 2m 39s | 3s | 98.1%↓ |

<br />



## ‼️ 트러블슈팅
### 1. application.yml 복사 권한 오류
 
```
cp: cannot create regular file './application.yml': Permission denied
```
 
**원인**
 
이전 빌드에서 root 권한으로 `application.yml` 이 생성된 후 jenkins 유저가 덮어쓸 수 없는 상태
 
```
Jenkins 워크스페이스 파일 소유권
application.yml → root 소유 (이전 빌드에서 생성)
jenkins 유저    → 쓰기 권한 없음 → cp 명령어 실패
```
 
**해결**
 
```bash
# 1. 워크스페이스 경로 확인
docker exec jenkins ls /var/jenkins_home/workspace/
 
# 2. 워크스페이스 전체 권한 변경
docker exec -u root jenkins chmod -R 777 /var/jenkins_home/workspace/docker-optimization/
 
# 3. Jenkins에서 다시 빌드 실행
```
