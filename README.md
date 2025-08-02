# springnovels-server
 <p>실시간 그림 퀴즈 게임 '스피드 드로잉' 서버입니다.</p>
 <p>Spring boot + WebSocket(Stomp) 기반으로 동작하며, 다수의 플레이어가 실시간으로 그림을 그리며 문제를 맞히는 게임을 제공합니다.</p>

## 🚀 기술 스택
### ⚡백엔드
<div>
<img src="https://img.shields.io/badge/spring boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/Spring JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white"> 
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> 
<img src="https://img.shields.io/badge/security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> 
<img src="https://img.shields.io/badge/intelli j-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
<img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white">
</div>

### ⚡인프라
<div>
  <img src="https://img.shields.io/badge/AWS EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">
  <img src="https://img.shields.io/badge/AWS ELB-8C4FFF?style=for-the-badge&logo=awselasticloadbalancing&logoColor=white">
  <img src="https://img.shields.io/badge/AWS RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">
  <img src="https://img.shields.io/badge/Elasticache-C925D1?style=for-the-badge&logo=amazonelasticache&logoColor=white">
</div>

### ⚡도구
<div>
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white">
</div>


<br/>

## 💻 프로젝트 소개

### ⚡기간
2025-06-26 ~ 2025-07-20
 <br/>

### ⚡주요기능(v1)

스웨거 - 서버 실행 후 http://localhost:8085/swagger-ui/index.html

|Method| MemberAPI | 기능 |
| ----- | ----- | ----- |
|POST|/api/v1/member/create|회원가입|
|POST|/api/v1/member/login|로그인|

 <br/>

|Method| QuizAPI | 기능 |
| ----- | ----- | ----- |
|POST|/api/v1/quiz/upload|퀴즈 문제 엑셀 업로드|
|GET|/api/v1/quiz/list|문제 리스트 확인|

 <br/>

|Method| GameAPI | 기능 |
| ----- | ----- | ----- |
|POST|/api/v1/game_room/create|게임방 생성|
|POST|/api/v1/game_room/join|게임 방 참가|

 <br/>

|Channel| 기능 | 비고 |
| ----- | ----- | ----- |
|/topic/game-room/${roomId}/info|게임방 정보 구독|게임방 세션|
|/topic/game-room/${roomId}/chat|게임방 채팅 메세지 구독|채팅 수신|
|/topic/game-room/${roomId}/drawing|그림 좌표 구독|그리기 수신|

 <br/>

 |Channel| 기능 | 비고 |
| ----- | ----- | ----- |
|/publish/game-room/${roomId}/chat|게임방 채팅 메세지 발행|채팅 전송|
|/publish/game-room/${roomId}/drawing|그림 좌표 발행|그리기 전송|

 <br/>



