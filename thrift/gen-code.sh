#!/usr/bin/env bash
thrift --gen java -out ../thrift-demo-java-api/src/main/java demo.thrift

thrift --gen py -out ../thrift-demo-py-api demo.thrift