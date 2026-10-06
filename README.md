# Two_Tier_App_using_Docker_and_Jenkins
Author: Harsh Aashiyani Date: 6th October 2026

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

## 3. AWS EC2 Instance Preparation
### 1. Launch EC2 Instance:
   Navigate to the AWS EC2 console.
   Launch a new instance using the Amazon Linux 2023 Kernal-6.18
   Select the t3.medium instance type.
   Create and assign a new key pair for SSH access.

### 2. Configure Security Group:
  - #### Create a security group with the following inbound rules:
   - Type: SSH, Protocol: TCP, Port: 22, Source: Your IP
   - Type: HTTP, Protocol: TCP, Port: 80, Source: Anywhere (0.0.0.0/0)
   - Type: Custom TCP, Protocol: TCP, Port: 8081 (for Java), Source: Anywhere (0.0.0.0/0)
   - Type: Custom TCP, Protocol: TCP, Port: 8080 (for Jenkins), Source: Anywhere (0.0.0.0/0)

### 3. Connect to EC2 Instance:
   Use SSH to connect to the instance's public IP address.
   ```
   ssh -i /path/to/key.pem ubuntu@<ec2-public-ip>
   ```

## 4. Install Dependencies on EC2   
- ### Step 1: Install Docker, Git & Buildx Plugin
```
# Update installed packages
sudo dnf update -y

# Install Docker and Git
sudo dnf install -y docker git

# Start and enable Docker service
sudo systemctl enable --now docker

# Add ec2-user to docker group
sudo usermod -aG docker ec2-user

# Install Docker Buildx v0.17.1
sudo mkdir -p /usr/local/lib/docker/cli-plugins

sudo curl -SL https://github.com/docker/buildx/releases/download/v0.17.1/buildx-v0.17.1.linux-amd64 \
  -o /usr/local/lib/docker/cli-plugins/docker-buildx

sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-buildx
```

- ### Step 2: Install Docker Compose
```
# Download the latest Docker Compose binary
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# Make it executable
sudo chmod +x /usr/local/bin/docker-compose
```

- ### Step 3: Install Java 17 & Jenkins
```
# Install Java 17 (Amazon Corretto)
sudo dnf install -y java-17-amazon-corretto-devel

# Import official Jenkins repo & key
sudo wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key

# Install Jenkins
sudo dnf install -y jenkins

# Grant Jenkins permission to run Docker commands
sudo usermod -aG docker jenkins

# Enable and start Jenkins
sudo systemctl enable --now jenkins
```

- ### Step 4: Initial Jenkins Setup:

Retrieve the initial admin password:
`sudo cat /var/lib/jenkins/secrets/initialAdminPassword`
Access the Jenkins dashboard at `http://<ec2-public-ip>:8080`.
Paste the password, install suggested plugins, and create an admin user.

- ### Step 5: Jenkins Pipeline Creation and Execution
#### 1. Create a New Pipeline Job in Jenkins:

- From the Jenkins dashboard, select New Item.
- Name the project, choose Pipeline, and click OK.

2. Configure the Trigger:
- Check Poll SCM
- In Schedule type `H/2 * * * *`
  
<img width="1862" height="835" alt="8" src="https://github.com/user-attachments/assets/a56a44b5-a254-4096-808f-0683d06f4668" />

####  3. Configure the Pipeline:

- In the project configuration, scroll to the Pipeline section.
- Set Definition to Pipeline script from SCM.
- Choose Git as the SCM.
- Enter your GitHub repository URL.
- Verify the Script Path is Jenkinsfile.
- Save the configuration.

<img width="1875" height="932" alt="4" src="https://github.com/user-attachments/assets/b0d0c484-fa99-44f3-8738-6c8f0aee9dad" />


#### 4. Run the Pipeline:
- Click Build Now to trigger the pipeline manually for the first time.
- Monitor the execution through the Stage View or Console Output.

<img width="1919" height="948" alt="1" src="https://github.com/user-attachments/assets/f0ef92e2-1587-4c22-883e-f3a35fb5c3fd" />

#### 5: Verify Deployment:
- After a successful build, your Flask application will be accessible at `http://<your-ec2-public-ip>:8081`

<img width="1906" height="865" alt="6" src="https://github.com/user-attachments/assets/4ae1f718-d3fb-417a-83e0-980b94cf6576" />

<img width="1818" height="914" alt="7" src="https://github.com/user-attachments/assets/7a025fce-2575-4c47-a7d3-39c4010dd928" />


## 5. Conclusion
- The CI/CD pipeline is now fully operational. Any git push to the main branch of the configured GitHub repository will automatically trigger the Jenkins pipeline, which will build the new Docker image and deploy the updated application, ensuring a seamless and automated workflow from development to production.

## 6. Infrastructure Diagram
<img width="1560" height="903" alt="cicd_pipeline_diagram" src="https://github.com/user-attachments/assets/4137f691-93b9-416c-b464-fe7ebe94a83f" />

