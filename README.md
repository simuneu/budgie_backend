# 🐥 BUDGIE — 개인 가계부 & 소비 분석 웹 앱
Budgie는 사용자의 소비 기록을 기반으로
예산 관리, 소비 패턴 분석, 실시간 FCM 알림 기능을 제공하는 <br/>
1인 프로젝트 가계부 웹 애플리케이션입니다.

번거롭게 직접 계산하지 않아도 과지출을 하지 않도록 누군가가 도와줬으면 좋겠다는 아이디어에서 시작되었습니다.<br/>
“위험한 소비 패턴을 미리 알려주는 친구”가 되는 것이 목표입니다.<br/>
## 👇배포 주소와 테스트 계정 
https://www.budgie.fit<br/>
ID: dino507782@gmail.com<br/>
PW: abcd1234!
## 🎬 주요 화면 미리보기
#### 🔐 로그인 및 로그아웃 (모바일)
<p align="center">
  <img src="./assets/gifs/login-logout.gif" width="260" />
</p>

#### 💰 목표 금액 수정 & 지출 관리 (CRUD)
<p align="center">
  <img src="./assets/gifs/goal-modify.gif" width="260" />
  <img src="./assets/gifs/crud.gif" width="480" />
</p>

#### 📊 통계 & 소비 분석
<p align="center">
  <img src="./assets/gifs/statistics.gif" width="480" />
  <img src="./assets/gifs/graph.gif" width="480" />
</p>

#### 🔔 실시간 FCM 알림 (읽기/삭제/전체 읽음)
<p align="center">
  <img src="./assets/gifs/alert.gif" width="260" />
</p>

## ✨ 주요 기능

<details>
<summary>🔐 인증 & 보안</summary>

- 이메일 회원가입 / 로그인
- 소셜 로그인 (네이버, 카카오)
- JWT Access & Refresh Token
- Redis 기반 Refresh Token 관리
- 비밀번호 변경 / 로그아웃

</details>

<details>
<summary>💰 가계부 기능</summary>

- 월별 지출 관리
- 카테고리별 통계 (파이 차트)
- 날짜별 지출 달력
- 지출 내역 CRUD
- 예산 설정 & 예산 대비 % 계산

</details>

<details>
<summary>📊 소비 패턴 분석</summary>

- 요일별 소비 패턴
- 월별 카테고리 TOP3
- 지난달 대비 지출 증감률
- 일 단위 누적 소비 그래프

</details>

<details>
<summary>🔔 FCM Push 알림</summary>

- 예산 70% / 80% / 90% 도달 시 자동 알림
- 지출 속도 분석 기반 위험 알림
- 스케줄러(9/12/14/18/22시) 기반 자동 분석 + 발송
- 알림 읽음 처리, 삭제 기능

</details>

<details>
<summary>🌐 배포 인프라</summary>

- AWS EC2 (Ubuntu)
- Nginx + HTTPS (Certbot)
- MySQL 8.0
- Redis (Docker)
- Firebase Cloud Messaging

</details>


## 👉기술 스택
### 🛠 Backend
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F.svg?logo=springboot&logoColor=white)
![Java 17](https://img.shields.io/badge/Java%2017-007396.svg?logo=openjdk&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F.svg?logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000.svg?logo=jsonwebtokens&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D.svg?logo=redis&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-59666C.svg)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C.svg?logo=hibernate&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?logo=gradle&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1.svg?logo=mysql&logoColor=white)
![FCM](https://img.shields.io/badge/FCM-FFCA28.svg?logo=firebase&logoColor=black)
![Flyway](https://img.shields.io/badge/Flyway-CC0200.svg?logo=flyway&logoColor=white)


### 🎨Frontend
![React](https://img.shields.io/badge/React-61DAFB.svg?logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6.svg?logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-646CFF.svg?logo=vite&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/TailwindCSS-06B6D4.svg?logo=tailwindcss&logoColor=white)
![Axios](https://img.shields.io/badge/Axios-5A29E4.svg?logo=axios&logoColor=white)
![Recharts](https://img.shields.io/badge/Recharts-0088FE.svg?logo=recharts&logoColor=white)
![React Router](https://img.shields.io/badge/React%20Router-CA4245.svg?logo=reactrouter&logoColor=white)

### ☁️Infra & DevOps
![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900.svg?logo=amazonaws&logoColor=white)
![Route53](https://img.shields.io/badge/Route53-8C4FFF.svg?logo=amazonaws&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639.svg?logo=nginx&logoColor=white)
![Certbot](https://img.shields.io/badge/Certbot-003A70.svg?logo=letsencrypt&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED.svg?logo=docker&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717.svg?logo=github&logoColor=white)


<details>
<summary>🏛 시스템 아키텍처</summary>

<div align="center">
  <img src="./assets/images/system.png" width="480" />
</div>

</details>

---

<details>
<summary>🚀 배포 아키텍처</summary>

<div align="center">
  <img src="./assets/images/deployment.png" width="480" />
</div>

</details>

---

<details>
<summary>🗂 ERD</summary>

<div align="center">
  <img src="./assets/images/erd.jpg" width="480" />
</div>

</details>


