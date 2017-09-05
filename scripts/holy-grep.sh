#!/bin/bash

# configuration variables, redefined at the bottom of the file
ROOT=.
LIMIT_NR_FILES=4

list_files ()
{
  find "$ROOT" | grep '\.java$'
}

find_regex ()
{
  REGEX=$1
  echo "regex: '$REGEX'"

  FILES=$(egrep -l "$REGEX" $(list_files))
  if [ $? != 0 ]; then
    echo "No files match"
    return 1
  fi

  echo -n $(wc -l <<< "$FILES") files match
  #for f in $FILES; do echo $f; done
  if [ $LIMIT_NR_FILES != 0 ]; then
    FILES=$(head -n $LIMIT_NR_FILES <<< "$FILES")
    echo ", showing matches on the first $LIMIT_NR_FILES:"
  else
    echo ":"
  fi

  egrep --color=auto "$REGEX" $FILES
  return 0
}

main ()
{
  echo "Configuration"
  echo "============="
  echo "root               = $ROOT"
  echo "max files per grep = $LIMIT_NR_FILES"

  echo
  echo
  echo "Keyword synchronized"
  echo "============="
  find_regex "synchronized"

  echo
  echo "Keyword volatile"
  echo "============="
  find_regex "volatile"

  echo
  echo "Method wait()"
  echo "============="
  find_regex "\.wait *\( *\)"

  echo
  echo "Method wait(timeout)"
  echo "============="
  find_regex "\.wait *\([^ ]+\)"

  echo
  echo "Method notify()"
  echo "============="
  find_regex "\.notify *\( *\)"

  echo
  echo "Method notifyAll()"
  echo "============="
  find_regex "\.notifyAll *\( *\)"

  echo
  echo "Weak memory (temptative)"
  echo "============="
  find_regex 'weak|memory model'

  echo
  echo "Package java.util.concurrent"
  echo "============="
  find_regex 'import * java.util.concurrent'

  echo
  echo "Package java.util.concurrent.atomic"
  echo "============="
  find_regex 'import * java.util.concurrent.atomic'

  echo
  echo "Package java.util.concurrent.locks"
  echo "============="
  find_regex 'import * java.util.concurrent.locks'

  echo
  echo "Package java.awt"
  echo "============="
  find_regex 'import * java.awt'

  echo
  echo "Package javax.swing"
  echo "============="
  find_regex 'import * javax.swing'

  echo
  echo "Package javax.servlet"
  echo "============="
  find_regex 'import * javax.servlet'

  echo
  echo "Thread pools created"
  echo "============="
  find_regex 'newfixedThreadPool|newSingleThreadExecutor|newCachedThreadPool|newWorkStealingPool'

  echo
  echo "Scheduled thread pools created"
  echo "============="
  find_regex 'scheduledThreadPool|singleThreadScheduledExectuor'
}

usage ()
{
  echo "Usage: holy-grep.sh PROJECT-ROOT"
}

# parse command line
if [ $# == 1 ]; then
  ROOT=$1
else
  usage
  exit 1
fi

# ROOT exists?
if ! test -d "$ROOT"; then
  echo "'$ROOT': not a directory!"
  exit 1
fi

# run the main function
main

