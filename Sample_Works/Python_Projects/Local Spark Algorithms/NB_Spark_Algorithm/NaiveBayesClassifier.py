#!/usr/bin/env python
# coding: utf-8

# In[1]:


from pyspark.context import SparkContext
from pyspark.sql.session import SparkSession
sc = SparkContext('local')
spark = SparkSession(sc)


# In[2]:


df = spark.read.csv("letter-recognition.data", inferSchema=True).toDF("letter", "xboxhoriz", "yboxvert", "widthbox", "heightbox","onpix","xbar",
     "ybar","meanxvariance","meanyvariance","meanxycorr","meanx*x*y","meanx*y*y","x-ege","xegvy","y-ege","yegvx")


# In[3]:


from pyspark.ml.linalg import Vectors
from pyspark.ml.feature import VectorAssembler


# In[4]:


vector_assembler = VectorAssembler(inputCols=["xboxhoriz", "yboxvert", "widthbox", "heightbox","onpix","xbar",
     "ybar","meanxvariance","meanyvariance","meanxycorr","meanx*x*y","meanx*y*y","x-ege","xegvy","y-ege","yegvx"],\
outputCol="features")
df_temp = vector_assembler.transform(df)


# In[5]:


df = df_temp.drop("xboxhoriz", "yboxvert", "widthbox", "heightbox","onpix","xbar",
     "ybar","meanxvariance","meanyvariance","meanxycorr","meanx*x*y","meanx*y*y","x-ege","xegvy","y-ege","yegvx")


# In[6]:


from pyspark.ml.feature import StringIndexer
l_indexer = StringIndexer(inputCol="letter", outputCol="LetterNumber")
df = l_indexer.fit(df).transform(df)


# In[13]:


splits = df.randomSplit([0.7, 0.3],1234)
train = splits[0]
test = splits[1]


# In[8]:


from pyspark.ml.classification import NaiveBayes
nb = NaiveBayes(labelCol="LetterNumber",featuresCol="features", smoothing=1.0,modelType="multinomial")
model = nb.fit(train)


# In[9]:


predictions = model.transform(test)
predictions.select("letter", "LetterNumber",
"probability", "prediction").show(150)


# In[11]:


from pyspark.ml.evaluation import MulticlassClassificationEvaluator


# In[14]:


evaluator =MulticlassClassificationEvaluator(labelCol="LetterNumber",predictionCol="prediction", metricName="accuracy")
accuracy = evaluator.evaluate(predictions)
print("Test set accuracy = " + str(accuracy))


# In[ ]:




