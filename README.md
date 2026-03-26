# 🐳 Docker 이미지 최적화
> 빌드 속도/이미지 크기/운영 관점을 고려한 Docker 이미지 최적화 전략을 4단계로 적용하고
> Jenkins CI/CD 파이프라인으로 단계별 수치를 자동 측정합니다.

<br />

---

## 👥 멤버

| <img src="https://github.com/caminobelllo.png" width="160px"> | <img src="https://github.com/Sungjun24s.png" width="160px"> | <img src="https://github.com/chaeyuuu.png" width="160px"> |
|:-------------------------------------------------------------:|:-----------------------------------------------------------:|:---------------------------------------------------------:|
|    __서가영<br>[@caminobelllo](https://github.com/caminobelllo)__    |          __박성준<br>[@Sungjun24s](https://github.com/Sungjun24s)__          |      __이채유<br>[@chaeyuuu](https://github.com/chaeyuuu)__      |

<br />

---

## 📋 목차

- [👥 멤버](#-멤버)
- [⚙️ 기술 스택](#️-기술-스택)
- [🗂️ 프로젝트 구조](#️-프로젝트-구조)
- [🚀 최적화 전략 — 4단계](#-최적화-전략--4단계)
- [🔧 Jenkins CI/CD 파이프라인](#-jenkins-cicd-파이프라인)
- [▶️ 실행 방법](#️-실행-방법)
- [🏁 결론](#-결론)
- [‼️ 트러블슈팅](#️-트러블슈팅)

<br />

---

## ⚙️ 기술 스택
> 스택 기술


<br />

---

## 🗂️ 프로젝트 구조
> 실제 프로젝트 구성에 따라 수정
```
docker-optimization-project/
├── Dockerfile                  # 최종 최적화 이미지 (Step 4)
├── docker/
│   ├── Dockerfile.naive        # 최적화 전 베이스라인
│   ├── Dockerfile.step1        # Step 1: 베이스 이미지 교체
│   ├── Dockerfile.step2        # Step 2: 멀티스테이지 빌드
│   └── Dockerfile.step3        # Step 3: .dockerignore 적용
├── .dockerignore               # 빌드 컨텍스트 필터링
├── build.gradle
└── src/
```


<br />

---

## 🚀 최적화 전략 — 4단계

### 📌 최적화 전 (Naive)

빌드 도구, 소스코드, JDK가 전부 최종 이미지에 포함된 상태

```dockerfile
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
RUN ./gradlew build -x test
CMD ["java", "-jar", "build/libs/app.jar"]
```

<br />

### 1️⃣ 베이스 이미지 교체

JAR 실행에는 컴파일러(JDK)가 불필요 → JRE로 교체

```dockerfile
# Before
 
# After
```

| | Before | After |
|--|--------|-------|
| 베이스 이미지 크기 |||


<br />

### 2️⃣ 멀티스테이지 빌드

빌드 환경과 실행 환경을 분리 → 최종 이미지에 JAR 파일만 포함

```dockerfile


```

최종 이미지에 Gradle · JDK · 소스코드 미포함

| | Before | After |
|--|-------|------|
| 이미지 크기 |  |  |
| 최종 이미지 내용 |  |  |


<br />

### 3️⃣ .dockerignore 추가

`docker build` 실행 시 불필요한 파일을 빌드 컨텍스트에서 제외
`.dockerignore` 없이 `COPY . .` 사용 시 `.git/` · `build/` 등 전체 전송

```gitignore
.git/
build/
.idea/
*.md
Dockerfile*
docker-compose*
Jenkinsfile
```

| | Before | After |
|--|------|----|
| 빌드 컨텍스트 크기 |  |  |
| 불필요한 캐시 무효화 | 발생 | 방지 |


<br />

### 4️⃣ 레이어 캐시 최적화

Docker는 레이어 변경 시 이하 레이어를 전부 재실행
→ 변경 빈도가 낮은 의존성을 위쪽, 소스코드를 아래쪽 레이어에 배치

```dockerfile
# Before: =

 
# After: 

```

| | Before | After |
|--|------|------|
| 소스 변경 시 재빌드 |  |  |



<br />

---


## 🔧 Jenkins CI/CD 파이프라인

### 파이프라인 흐름

```
GitHub Push (webhook)
        │
        ▼
┌───────────────────────────────────────────────────┐
│                  Jenkins Pipeline                  │
│                                                   │
│  Stage 1. Checkout    소스코드 체크아웃             │
│  Stage 2. Build JAR   Gradle 빌드 → JAR 생성       │
│  Stage 3. Docker Build  이미지 빌드 + 크기/시간 측정│
│  Stage 4. Docker Push   Docker Hub push            │
│  Stage 5. Deploy      컨테이너 실행                 │
│  Stage 6. Cleanup     불필요한 이미지 정리           │
└───────────────────────────────────────────────────┘
```

<br />

### Stage별 상세 동작

**Stage 2 — Build JAR**
```bash
chmod +x gradlew
START=$(date +%s)
./gradlew bootJar --no-daemon -x test
END=$(date +%s)
echo "▶ Gradle 빌드 소요 시간: $((END - START))초"
```
빌드 성공 시 `build/libs/*.jar` 파일 크기 자동 출력

**Stage 3 — Docker Build**
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

**Stage 4 — Docker Push**
```bash
# Jenkins Credentials에 저장된 Docker Hub 계정으로 로그인
echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
docker push ${IMAGE_NAME}:${IMAGE_TAG}
docker push ${IMAGE_NAME}:latest
```

**Stage 5 — Deploy**
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

### Jenkins 환경 구성

Jenkins를 Docker 컨테이너로 실행하고 호스트의 Docker를 공유

```bash
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts
```

> `/var/run/docker.sock` 마운트로 Jenkins 컨테이너 내부에서 `docker build` 실행 가능


<br />

## ▶️ 실행 방법

> 실행 방법 작성


<br />

---


## 🏁 결론

### 단계별 측정 결과

> Jenkins 파이프라인에서 자동 측정한 실제 수치

> 빌드 완료 후 실제 수치로 채울 것

| 단계 | 이미지 크기 | 재빌드 시간 | 개선 포인트 |
|------|-----------|-----------|-----------|
| Naive | - | - | 베이스라인 |
| 베이스 이미지 교체 | - | - | 크기 감소 |
| 멀티스테이지 빌드 | - | - | 크기 감소 |
| .dockerignore | - | - | 컨텍스트 정리 |
| 레이어 캐시 최적화 | - | - | 재빌드 속도 |


### 최적화 전/후 핵심 수치

| | Naive | 최종 최적화 | 개선율 |
|--|-------|-----------|------|
| 이미지 크기 | -MB | -MB | -%↓ |
| 소스 변경 시 재빌드 | -분 | -초 | -%↓ |
| 빌드 컨텍스트 크기 | -MB | -MB | -%↓ |

> 빌드 완료 후 실제 수치로 채울 것



<br />

---

## ‼️ 트러블 슈팅
> 트러블 슈팅 작성
