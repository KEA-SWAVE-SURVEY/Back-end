import pymysql
import psycopg2
import time
import json
from datetime import datetime
from mlxtend.frequent_patterns import apriori
from mlxtend.preprocessing import TransactionEncoder
import argparse
import pandas as pd

def analyze_for_all():
   sourceConnect = pymysql.connect(
      host='localhost',
      user='root',
      password='admin',
      db='surveydb'
   )

#    parser = argparse.ArgumentParser(description='CLI argument parser')
#    parser.add_argument('document_id', help='input parameter')
#    args = parser.parse_args()

   #SQL 예제 : SQL 테이블 둘러보고 다 가져오기
   sourceCursor = sourceConnect.cursor()

   rdb = 'show tables'
   sourceCursor.execute(rdb)



   resultSource = sourceCursor.fetchall()

   #print('Results before insert in source mysql database ', resultSource)

   temp = resultSource
   for i in range(0, len(temp)):
       rdb = 'select * from '+''.join(resultSource[i])
       sourceCursor.execute(rdb)
       resultSources = sourceCursor.fetchall()
       #print(resultSources)
   #print()
   #끝
   rdb = f'SELECT survey_answer_id FROM SURVEY_ANSWER where survey_document_id=1'
   sourceCursor.execute(rdb)
   resultSources = sourceCursor.fetchall() # 데이터값 ((1, '치킨', 1), (2, '피자', 1), (3, '김치', 1), (4, 'bhc', 2), (5, 'bbq', 2), (6, '교촌', 2)))
   print(resultSources)
   temp = []
   for i in resultSources:
      temp.append(i[0])
      print(i[0])
   print(temp)
   #input()
   tempResult = []
   for i in temp:
        rdb = f'SELECT check_answer_id FROM QUESTION_ANSWER where survey_answer_id={i}'
        sourceCursor.execute(rdb)
        resultSources = sourceCursor.fetchall() # 데이터값 ((1, '치킨', 1), (2, '피자', 1), (3, '김치', 1), (4, 'bhc', 2), (5, 'bbq', 2), (6, '교촌', 2)))
   
        answer = []
        for t in resultSources:
            answer.append(t[0])
            print(t[0])
        tempResult.append(answer)
   print(tempResult)
   resultSources = tempResult
   #print('데이터 값: ',resultSources)

#    resultSources =  (
#    [1,2, 3, 1000,45.4124214],
#        ['2','햄버거', '좋음', '찬성','찬성'],
#       ['여성','담배', '보통', '반대','반대'],
#        ['여성','짜장면', '싫음', '반대','찬성'],
#        ['남성','짬뽕', '싫음', '찬성','찬성'],
#        ['남성','피자', '싫음', '반대','반대'],
#        ['남성','짬뽕', '싫음', '찬성','찬성'],
#        ['남성','짬뽕', '싫음', '찬성','기권'],
#        ['남성','짬뽕', '싫음', '찬성','기권'],
#        ['남성','짬뽕', '싫음', '찬성','찬성'],
#        ['남성','짬뽕', '싫음', '찬성','반대'],
#        ['남성','짬뽕', '싫음', '찬성','기권'],
#        ['남성','짬뽕', '싫음', '찬성','기권'],
#        ['남성','짬뽕', '싫음', '찬성','찬성'],
#        ['남성','피자', '싫음', '반대','반대']
#       )
   '''
   1. apriori 구현
   2. 코드 구현에 필요한 것(특정 문항 집은놈 고르기)
   자바 (데이터 주기) -> 파이썬 (데이터 자바로 돌려주기) -> 자바 (데이터 받기)
   해야 할것: 모든 값들 정렬 및 최빈값 구하기 / JSON 형태로 진행예정
   ((1, '치킨', 1), (2, '피자', 1), (3, '김치', 1), (4, 'bhc', 2), (5, 'bbq', 2), (6, '교촌', 2))
   이런 형태로 나옴.
   모든 응답은 모두 동일한 문제들이므로, 첫 응답의 번호 개수만큼 딕셔너리 생성
   '''  
   totalCount = len(resultSources)
   print('Total Count of response : ', totalCount)
   # apriori 구현을 위하여 문항별 마스킹
   for i in range(0,len(resultSources)):
      for t in range(0,len(resultSources[i])):
         resultSources[i][t] = f'{t}_{resultSources[i][t]}'
   print('ResultSources(The list of responses) : ',resultSources)
   answers = []
   for i in range(0,len(resultSources[0])):
       # i = i번째 문항답. 이를 모두 추려야함
       answers.append([])
       for t in range(0, len(resultSources)):
           answers[i].append(resultSources[t][i])

   for i in range(0,len(answers)):
       print('답변번호별 나눔',answers[i])
   temp = list(resultSources)
   for i in range(0, len(temp)):
      print('See!', temp[i], temp[i][0][0:1])

   print('답변 번호별 모음 리스트: ',answers)
   counterForAnswers = []
   print(answers)
   for  i in range(0, len(answers)):
       counterForAnswers.append({})
       for t in answers[i]: #답변별로 분류
           print(i, ' ' ,t, '번째 답변  : ',answers[i], ' ',counterForAnswers[i])
           if t in counterForAnswers[i]:
               counterForAnswers[i][t] +=1
           else:
               counterForAnswers[i][t] = 1
   print('CounterForAnswers: ',counterForAnswers) # 각 문항별로 숫자 세고, 그것들 기반으로 나누기
   answerKind = []
   for i in range(0,len(counterForAnswers)):
      answerKind.append(list(counterForAnswers[i].keys()))
      print(i ,' 번째 문항 답안들 : ', answerKind[i])

   print('Answerkind: ',answerKind) # 문항별 답안수.


   responsePerAnswer = [] # 모든 문항별로 분류된 매우 큰 리스트
   '''
   큰 문항 리스트 자료구조
   이후 apriori도 이렇게 구현되어 있고, 지지도, 항목 순으로 상위 3개 정렬됨.

   [ # 첫 층: 껍데기(접근가능하도록)설문 다큐먼트 id
      [ # 문제1. 성별 (성별문항 id)
         [ choiceId문제 1-1.남성
            [[[1.0, ['0_1']], [0.8461538461538461, ['2_싫음']]],...
          ... (리스트)
         ] #세번째 층, 각 답변별로 나뉨
         [ 문제 1-2 여성

         ]
      ], # 두번째 층, 각 문항별로 나뉨
   [
   ....
   ]

   ]
   '''
   for i in range(0,len(answerKind)):
      responsePerAnswer.append([]) # 문항별 큰 칸 (두번째 층)
      for k in range(0,len(answerKind[i])):
         responsePerAnswer[i].append([])
         print(i, ' ',k,' : ',answerKind[i][k]) # 문항별 개수출력, k는 문항내용들 / i는 몇번째 문항인지
         for p in range(0, len(resultSources)): # 모든 문항 한번씩 순회. 필요한 것: 비교
            if resultSources[p][i] == answerKind[i][k]:
               print('i ',i,'k ',k,'p :',p, ' ' ,resultSources[p][i],' ', answerKind[i][k], ' ',resultSources[p]) # append 하게하면될듯
               responsePerAnswer[i][k].append(resultSources[p])
            #if resultSources[p][int()]
   for i in range(0,len(responsePerAnswer)):
      for k in range(0,len(responsePerAnswer[i])):
         for p in range(0,len(responsePerAnswer[i][k])):
            print('i : ',i, '|','k ',k,'p',p,'|', responsePerAnswer[i][k][p][i] , '|',  responsePerAnswer[i][k][p]) # i :  4(문제 번호수) | k(답변 번호수)  2 p(갯수에 따른 분류) 0 | 4_기권 | ['0_남성', '1_짬뽕', '2_싫음', '3_찬성', '4_기권']
   #해야 할것: 문항별 APRIORI 구현


   ultimateApriori = []
   for i in range(0,len(responsePerAnswer)):
      ultimateApriori.append([])
      for k in range(0,len(responsePerAnswer[i])):
         ultimateApriori[i].append([])
         te = TransactionEncoder()
         print(responsePerAnswer[i][k])
         te_ary = te.fit(responsePerAnswer[i][k]).transform(responsePerAnswer[i][k])
         df = pd.DataFrame(te_ary, columns=te.columns_)
         frequent_itemsets = apriori(df, min_support=0.5, use_colnames=True)
         frequent_itemsets =frequent_itemsets.nlargest(5, 'support')
         tempList = []
         for test, row in frequent_itemsets.iterrows():
            support = row['support']
            itemset = list(row['itemsets'])
            #print('T!',support,  '|', itemset )
            tempList.append([support, itemset])
         ultimateApriori[i][k].append(tempList)
            #print(responsePerAnswer[i][k])# 각 문항별 apriori

   for i in range(0,len(ultimateApriori)):
      for t in range(0, len(ultimateApriori[i])):
         for p in range(0, len(ultimateApriori[i][t])):
            print(f'i {i} t {t} p {p} Test: {answerKind[i][t]}|',ultimateApriori[i][t][p])

   #이하는 기존 최빈값 코드

   '''

   남은 것 : 1. 최빈값 구하기 ( 위코드 기반으로 ) /  apriori는 되었으니 프론트엔드 작업실시 및 SQL 조율




      for i in range(0,len(counterForAnswers)):
          print(i+1,' 번째 문항 통계:', counterForAnswers[i])

      reversedCounterForAnswers = []
      for  i in range(0, len(answers)):
          reversedCounterForAnswers.append({})
          for t in counterForAnswers[i]: #키별 부여
              print('#',t, counterForAnswers[i],counterForAnswers[i][t] )
              try: reversedCounterForAnswers[i][counterForAnswers[i][t]].append(t)
              except: reversedCounterForAnswers[i][counterForAnswers[i][t]] = [t]

      print('reversed CounterForAnswers : ', reversedCounterForAnswers) # 최빈값 숫자로 문제별로 역정렬

      maxValue = {}
      temp = 0
      for i in range(0,len(counterForAnswers)):
          print('Look!',counterForAnswers[i])
          if temp < max(reversedCounterForAnswers[i].keys()):
               temp=max(reversedCounterForAnswers[i].keys())
               maxValue = {  i : reversedCounterForAnswers[i][temp]}
          elif temp == max(reversedCounterForAnswers[i].keys()): #최빈값이랑 같을시 추가
              print('$',reversedCounterForAnswers[i][temp])
              maxValue[i]  =reversedCounterForAnswers[i][temp]
              print('!!!',maxValue) # 최빈값 확인용 로그
          print('behold!', maxValue.keys())
          for p in list(maxValue.keys()):
              print(p, ' | ', maxValue[p] , ' | ',temp,' | ',reversedCounterForAnswers[i][max(reversedCounterForAnswers[i].keys())] ,' | '  , max(reversedCounterForAnswers[i].keys()) )
      print('MV :',maxValue) # value : 최빈값 , maxValue: n 번쨰 키 문항에 [리스트]의 답변들이 최빈값임.
      print('Most record : ',temp) # 최빈값의 레코드

      print('Most value: ' ,list(maxValue.keys())) # 문항번호 내용
      print('Value :',list(maxValue.values())) # 문항답안 내용

      print(answers)
      mostValues = []
      resultSources = list(resultSources)
      print('FA : ')
      for i in resultSources:
         for p in list(maxValue.keys()):
            print('p:',p ,' ',maxValue[p], ' i : ', i[p])
            if i[p] == list(maxValue[p])[0]:
               mostValues.append(i)
      for i in mostValues:
         print('mostvalue i ' ,i)

      #mostValues : arpiori 사용용으로 만든 최빈값.
      te = TransactionEncoder()
      te_ary = te.fit(mostValues).transform(mostValues)
      df = pd.DataFrame(te_ary, columns=te.columns_)

      frequent_itemsets = apriori(df, min_support=0.1, use_colnames=True)

      frequent_itemsets =frequent_itemsets.nlargest(5, 'support')
      print(frequent_itemsets)

      result = []
      for test, row in frequent_itemsets.iterrows():
          support = row['support']
          itemset = list(row['itemsets'])
          print('T!',itemset, list(maxValue.values())[0], '|', itemset )
          if list(maxValue.values())[0][0] not in itemset: # 최빈값 포함된 의미없는 결과 제외
              result.append([support, itemset])
      print(result) # 최빈값 제외 apriori 테스트값:  [[지지율(비율) , 내용 ]] 순으로 정렬됨.
      for i in result:
         print(i)
         # 해야 할것 : 문항별로 분석해서,
      print(args.document_id)
   '''

   return ultimateApriori

def main():
   input_param = analyze_for_all()
   return input_param
if __name__ == '__main__':
   result = main()
   print('testresult ')
   exit(result)

# apriori 구현 이후 필요시 사용할 문항별 디마스킹 (원상복구) 방법 : slice [2: ] 하기
'''
for i in range(0,len(ultimateApriori)):
   for t in range(0, len(ultimateApriori[i])):
      print(ultimateApriori)
for p in range(0, len(resultSources)):
   print(resultSources[p])
questionID
고민중인 것: 만약 최빈값이 1번문제 3개응답, 3번문제 3개응답이 같다와 같이 다른 문항에도 나온다면? 그걸 일일히 해야 하나?
-> 구현은 가능한데...
-> 5.3 회의결과 최빈값만 하기로

의자입니다. 의자와 같이 같은 대상인데, 다른 결과인걸 어떻게 나누나?
-> 객관식만 한다고 했는데, 이부분은 "일단은" 가능하겠지만 안되게 (즉 where = 식으로) 설정 가능

이후 진행예정사항
교차분석
비교분석
연관분석 구현
MySQL 쿼리연동
현재 진행예정: 제일 많은 값 1개만
2개 이상 최빈값: 1개 유기

연관분석 하면서 깨달은것
-> apriori 하면 다른 문항마다 같은 값이 있으면 (기저귀 / 맥주처럼 unique와 다르게) 안되므로 문항마다 라벨을 붙여서 unique하게, prefix를 붙이면 해결될듯.

Ex : '좋음' - > '1좋음' 식으로...

+ 추가적으로 필요한 것 : Chi test를 위해서는 최빈값 문항상 두번째 최빈값(위 예제에서는 여성이 그 예)을 구해서, 그 최빈값과 진행시켜야 할듯?
아니면 이건 어떻

게 할지 논의가 필요함 .
생각한 것대로 할지, 다른 좋은 방버이 있을지..

최종: 모든 값을 모두 apriori / t분석 brute force(모든 조합)

for i in range(0,len(resultSources)):
   for t in range(0,len(resultSources[i])):
      print(intresultSources[i][t]+1)

      스트링 저장 형태 :( 연관관계 갯수, 연관관계 갯수 x ( 지지율,  question_id, choice_id , 연관값 개수, 연관된 개수 x (연관된 question_id , 연관된 choice_id))
,
       )
'''