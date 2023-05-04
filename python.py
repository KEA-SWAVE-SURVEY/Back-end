import pymysql
import psycopg2
import time
import json
from datetime import datetime
from mlxtend.frequent_patterns import apriori
from mlxtend.preprocessing import TransactionEncoder
import pandas as pd

'''
TODO : 파라미터 받아오는 형태로 작업해서, 속도올리기 코드완성먼저진행
'''

sourceConnect = pymysql.connect(
   host='localhost',
   user='root',
   password='admin',
   db='surveydb'
)

#SQL 예제 : SQL 테이블 둘러보고 다 가져오기
sourceCursor = sourceConnect.cursor()
parameter_documentid = '1' # 파라미터예정
rdb = 'SELECT survey_answer_id FROM SURVEY_ANSWER where survey_document_id='+parameter_documentid
sourceCursor.execute(rdb)
resultSource = sourceCursor.fetchall()
resultSource = list(resultSource)
print(list(resultSource[0])[0])  # 예상 : id 여러개 온것들(1, 2, 3.... )

answerList= [] #답변 초안, 내부에 [[]] 형태로 작업예정

for i in range(0, len(resultSource)):
   print(list(resultSource[i])[0])
   rdb = f'SELECT check_answer FROM QUESTION_ANSWER where survey_answer_id={list(resultSource[i])[0]}'
   print(rdb)
   sourceCursor.execute(rdb)
   answerUserSource = sourceCursor.fetchall()
   answerUserSource = [list(t)[0] for t in answerUserSource]
   answerList.append(answerUserSource)
print('Answer prototype : ', answerList)


titleList= [] #답변 초안, 내부에 [[]] 형태로 작업예정

print(list(resultSource[0])[0])
rdb = f'SELECT question_title FROM QUESTION_ANSWER where survey_answer_id={list(resultSource[i])[0]}'
print(rdb)
sourceCursor.execute(rdb)
answerUserSource = sourceCursor.fetchall()
answerUserSource = [i[0] for i in answerUserSource]
titleList = answerUserSource
print('Answer prototype : ', titleList)