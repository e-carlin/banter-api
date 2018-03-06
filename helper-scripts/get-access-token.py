import boto3

client = boto3.client('cognito-idp')

response = client.admin_initiate_auth(
    UserPoolId='us-east-1_M0GwiV1g7',
    ClientId='5983dgn1d2jum78or5mg6cqsk9',
    AuthFlow='ADMIN_NO_SRP_AUTH',
    AuthParameters={
        'USERNAME': 'evforward123@gmail.com',
        'PASSWORD' : '12345678'
    }
)

print('AccessKey: {}',response['AuthenticationResult']['AccessToken'])