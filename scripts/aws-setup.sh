#!/bin/bash
# Run these commands in AWS CLI to set up ECR repositories

# Set your AWS region
AWS_REGION="us-east-1"

# Create ECR repositories
aws ecr create-repository \
    --repository-name nutrifit-backend \
    --region $AWS_REGION \
    --image-scanning-configuration scanOnPush=true

aws ecr create-repository \
    --repository-name nutrifit-frontend \
    --region $AWS_REGION \
    --image-scanning-configuration scanOnPush=true

# Get your ECR registry URL (you'll need this later)
echo "Your ECR Registry URL:"
aws sts get-caller-identity --query Account --output text | xargs -I {} echo "{}.dkr.ecr.${AWS_REGION}.amazonaws.com"
