#!/bin/sh
set -eu
umask 077

file="${GRADLE_USER_HOME:-$HOME/.gradle}/gradle.properties"

token=$(aws codeartifact get-authorization-token \
    --domain yossibank --domain-owner 724669215656 --region ap-northeast-1 \
    --query authorizationToken --output text)

mkdir -p "$(dirname "$file")"
touch "$file"

rest=$(grep -v '^codeArtifactPassword=' "$file" || true)
{
    [ -n "$rest" ] && printf '%s\n' "$rest"
    printf 'codeArtifactPassword=%s\n' "$token"
} > "$file.new"
cat "$file.new" > "$file"
rm "$file.new"

echo "CodeArtifact のトークンを $file に書き込みました（12 時間有効）"
