#!/bin/bash

TABLE_NAME=Accounts
KEY=userSub

aws dynamodb scan --table-name $TABLE_NAME --attributes-to-get "userSub" --query "Items[].userSub.S" --output text --endpoint-url http://localhost:8000 | tr "\t" "\n" | xargs -t -I keyvalue aws dynamodb delete-item --table-name $TABLE_NAME --key '{"userSub": {"S": "keyvalue"}}' --endpoint-url http://localhost:8000