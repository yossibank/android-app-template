#!/bin/sh
set -eu
umask 077

file="${GRADLE_USER_HOME:-$HOME/.gradle}/gradle.properties"

codeartifact() { sed -n "s/^codeArtifact\.$1=//p" gradle.properties; }

token=$(aws codeartifact get-authorization-token \
    --domain "$(codeartifact domain)" --domain-owner "$(codeartifact owner)" --region "$(codeartifact region)" \
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
