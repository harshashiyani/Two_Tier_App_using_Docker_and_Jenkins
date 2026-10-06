# Two_Tier_App_using_Docker_and_Jenkins
Two tier application using Docker and  Jenkins

## 1. Project Overview
This document outlines the step-by-step process for deploying a 2-tier web application (Java + MySQL) on an AWS EC2 instance. The deployment is containerized using Docker and Docker Compose. A full CI/CD pipeline is established using Jenkins to automate the build and deployment process whenever new code is pushed to a GitHub repository.

## 2. Architecture Diagram
```
+--------------------+        +---------------------+        +-----------------------------------+
|  Developer (Local) | -----> | GitHub Repository   | -----> | AWS EC2 Instance                  |
|  (Pushes code)     |        | (Source Code Mgmt)  |        |                                   |
+--------------------+        +---------------------+        | +-------------------------------+ |
                                                             | | Jenkins Server                | |
                                                             | | 1. Clones Repo                | |
                                                             | | 2. Builds Java Docker Image   | |
                                                             | | 3. Runs Docker Compose        | |
                                                             | +-------------------------------+ |
                                                             |                 |                 |
                                                             |                 | Deploys         |
                                                             |                 v                 |
                                                             | +-------------------------------+ |
                                                             | | Application Containers        | |
                                                             | |                               | |
                                                             | | +---------------------------+ | |
                                                             | | | Java (Spring Boot)        | | |
                                                             | | +---------------------------+ | |
                                                             | |               |               | |
                                                             | |               v               | |
                                                             | | +---------------------------+ | |
                                                             | | | MySQL Container           | | |
                                                             | | +---------------------------+ | |
                                                             | +-------------------------------+ |
                                                             +-----------------------------------+
```

## 3. Step 1: AWS EC2 Instance Preparation
###1. Launch EC2 Instance:
   Navigate to the AWS EC2 console.
   Launch a new instance using the Amazon Linux 2023 Kernal-6.18
   Select the t3.medium instance type.
   Create and assign a new key pair for SSH access.

###2. Configure Security Group:
   Create a security group with the following inbound rules:
   Type: SSH, Protocol: TCP, Port: 22, Source: Your IP
   Type: HTTP, Protocol: TCP, Port: 80, Source: Anywhere (0.0.0.0/0)
   Type: Custom TCP, Protocol: TCP, Port: 8081 (for Java), Source: Anywhere (0.0.0.0/0)
   Type: Custom TCP, Protocol: TCP, Port: 8080 (for Jenkins), Source: Anywhere (0.0.0.0/0)

###3. Connect to EC2 Instance:
   Use SSH to connect to the instance's public IP address.
   ```
   ssh -i /path/to/key.pem ubuntu@<ec2-public-ip>
   ```
   
