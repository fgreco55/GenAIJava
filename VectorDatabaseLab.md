# Vector Database - Lab exercises

1. Create a schema for a collection of “chunks” with a vector DB (eg, Milvus).  Add document, URL, date, etc.  Try using a sentence as a “chunk” using the sentence parser shown earlier.  VectorDatabase/CreateCollection

2. Insert data “chunks” from a text file into this collection.

3. Using your Chatbot class, get a string from the user and find the top N most-similar strings in the VDB.  Add those strings to the user prompt and retrieve a completion.  VectorDatabase/SearchCollection 

4. Change your FileChatbot so it reads related information from the VDB instead of files.  Continue to use a sentence as a “chunk” using the sentence parser shown earlier.  See PrivateChatbot/PrivateChatbot
