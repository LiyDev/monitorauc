#!/usr/bin/env sh
set -e
cd "$(dirname "$0")"
rm -rf out
mkdir -p out
javac -encoding UTF-8 -d out $(find src/main/java -name '*.java')
exec java -cp out br.ucsal.monitorauc.App "$@"
