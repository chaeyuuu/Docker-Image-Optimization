# 🐳 Docker 이미지 최적화
> 빌드 속도/이미지 크기/운영 관점을 고려한 Docker 이미지 최적화 전략을 4단계로 적용하고
> Jenkins CI/CD 파이프라인으로 단계별 수치를 자동 측정합니다.

## 👥 멤버

| <img src="https://github.com/caminobelllo.png" width="160px"> | <img src="https://github.com/Sungjun24s.png" width="160px"> | <img src="https://github.com/chaeyuuu.png" width="160px"> |
|:-------------------------------------------------------------:|:-----------------------------------------------------------:|:---------------------------------------------------------:|
|    __서가영<br>[@caminobelllo](https://github.com/caminobelllo)__    |          __박성준<br>[@Sungjun24s](https://github.com/Sungjun24s)__          |      __이채유<br>[@chaeyuuu](https://github.com/chaeyuuu)__      |

## ⚙️ 기술 스택
> 스택 기술

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
 
---

### 1️⃣ 베이스 이미지 교체

JAR 실행에는 컴파일러(JDK)가 불필요 → JRE로 교체

```dockerfile
# Before
 
# After
```

| | Before | After |
|--|--------|-------|
| 베이스 이미지 크기 |||
 
---

### 2️⃣ 멀티스테이지 빌드

빌드 환경과 실행 환경을 분리 → 최종 이미지에 JAR 파일만 포함

```dockerfile


```

최종 이미지에 Gradle · JDK · 소스코드 미포함

| | Before | After |
|--|-------|------|
| 이미지 크기 |  |  |
| 최종 이미지 내용 |  |  |
 
---

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
 
---

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
 
---

## 📊 단계별 측정 결과

> Jenkins 파이프라인에서 자동 측정한 실제 수치

| 단계 | 이미지 크기 | 재빌드 시간 | 개선 포인트 |
|------|-----------|-----------|-----------|
| Naive | - | - | 베이스라인 |
| 1️⃣ 베이스 이미지 교체 | - | - | 크기 감소 |
| 2️⃣ 멀티스테이지 빌드 | - | - | 크기 감소 |
| 3️⃣ .dockerignore | - | - | 컨텍스트 정리 |
| 4️⃣ 레이어 캐시 최적화 | - | - | 재빌드 속도 |

> 빌드 완료 후 실제 수치로 채울 것
 
---

## ▶️ 실행 방법

> 실행 방법 작성

### 4. 수치 확인

파이프라인 실행 완료 후 콘솔 출력에서 단계별 결과 확인

```

```
 
---

## ‼️ 트러블 슈팅
> 트러블 슈팅 작성