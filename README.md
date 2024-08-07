# INTRODUCTION TO GENAI FOR JAVA DEVELOPERS
This repo is a companion to the "Introduction to GenAI for Java Developers" course by Frank Greco.

## GOALS OF THE COURSE
Empower Java software developers with the foundational knowledge, concepts, and basic skills needed to responsibly 
harness the capabilities of generative AI tools for various aspects of the overall production software process.
This course is focused on learning the fundamentals of AI/ML, primarily generative AI by
using Java tools.

We will use a collection of software and GenAI APIs in this course, but the information and techniques 
learned can be applied to other ML tools, LLMs, and APIs.

We'll learn how to create a Chatbot from scratch. Even though Machine Learning is more than just Chatbots, learning about Chatbots helps you understand GenAI foundations.  


## PREREQUISITES
* Basic Java programming knowledge (3-5+ years of experience)
* Simple debugging skills
* Familiarity with basic software development and architecture concepts
* Experience with API integration
* Hardware: Intel 4-core+ or Apple silicon, 8G+ RAM

## SOFTWARE TO RUN THE EXAMPLES IN THIS REPO
* Very basic knowledge of git/github
* MacOS 10.14+, Windows 10/11 with WSL 2, Linux
* IDE (in-class examples will use IntelliJ, but others can be used)
* OpenAI account and API key [see below]
* LangChain4j
* Docker Desktop (Mac/Windows), Docker 19 (Linux)
* Milvus vector db [see below]

## HOW TO GET AN OPENAI API KEY
* Visit https://platform.openai.com/account/api-keys
* Click on the "Create new secret key" button
* Give the key a name to help you remember its purpose
* Select the type of Permissions (we can use ‘All’ for our labs)
* Click on the  "Create secret key" button
* Set the environment variable "OPENAI_API_KEY" in IntelliJ with your OpenAI key.
  [Run->Edit Configurations->Environment Variables]

## INSTALLING MILVUS WITH DOCKER COMPOSE
  https://milvus.io/docs/v2.3.x/install_standalone-docker-compose.md

## BASIC STEPS TO INSTALL AND RUN MILVUS (vector db)
  Install Docker - https://docs.docker.com/get-docker/
  
  Download YAML file:
```
  $ wget https://github.com/milvus-io/milvus/releases/download/v2.3.12/milvus-standalone-docker-compose.yml -O docker-compose.yml
``` 
  Run Milvus:  
```
  $ sudo docker compose up -d		# might not need sudo, and “down” stops.
  Creating milvus-etcd  ... done
  Creating milvus-minio ... done
  Creating milvus-standalone ... done
```

