@echo off
setlocal enabledelayedexpansion
set count=0
for /R %%i in (*) do (
    set /A count+=1
)
echo Total Files: !count!