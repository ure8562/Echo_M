# Echo_M
시리얼 자작 앱 제작
적용되는 모델
 FT232R 
 FT2232H 
 FT4232H 
 FT232H 
 FT231X 
 Arduino 
 Teensyduino  
 Silabs CP210x 
 Qinheng CH340 
 Prolific PL2303 
 ARM mbed 

추가하기 위해서는 xml 폴더 하위 device_filter.xml 에서 추가가능
설정값 고정 (MainActivity 에서 setParameters에서 조절 가능)
baudrate : 921600
data : 8bit
stopbit : 1bit
parity : none

프로그램 소개
휴대폰에 USB 연결 후 USB 권한 승인 해줘야한다.
권한 승인 후 앱에서 CONNECT를 누를시 연결에 성공되면 CONNECT는 DISCONNECT로 변경되며
SEND버튼은 활성화 된다.
SEND버튼으로 헥사값만 쓸수있다.
예) AEAE, 97, 0101AEAECD 등
RECV로 byte 를 hex로 변경하여 출력한다.
DISCONNECT시 연결이 종료된다.
    