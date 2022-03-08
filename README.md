# ExcelConverter



### TODO
 - [ ] Excel파일이 5MB 이상이면 힙이 터져서 프로그램이 종료된다. 버퍼단위로 엑셀 파일을 읽어서 스케쥴러로 처리를 해줘서 엑셀파일을 읽도록 작성해야?
 - POI는 엑셀 97~2003은 HSSF 타입으로 2007은 XSSF타입으로 WorkBook 을 생성관리하는데 XSSF는 모든 데이터를 메모리에 로드해서 사용한다고 합니다. 때문에 파일크기가 5MB정도만 되도 Java heap memory 문제를 일으킵니다.
 - [ ] Jacoco 사용해서 Test Coverage 확인하기