# Chatbot - Lab exercises

1. Create a simple chatbot that gets a prompt from the user and gets a completion from an LLM.  Evaluate the results.  Starter code is in GenAIJava/Chatbot/Begin

2. Modify the chatbot to maintain a conversation context.  Add the ASSISTANT messages (i.e., the previous completion messages) to the prompt sent to the LLM as part of the context.

3. Now add the USER messages to the prompt that is sent to the LLM as part of the context.  With the USER and ASSISTANT messages history added to the context sent to the LLM, evaluate the results.  Notice how more “conversational” the chat session proceeds.
 
You now have a Chatbot class that maintains a separate context and conversation.  You can create multiple instances, with each one having a different persona, thread of conversation, and output format.
