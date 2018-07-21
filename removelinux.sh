if [ -d /usr/local/bin/zeta-out ]   # for file "if [-f /home/rama/file]" 
then 
    sudo rm -r  /usr/local/bin/zeta-out
   
else
    echo "zeta is not installed."
fi

if [ -e /usr/local/bin/zeta ]
then
    sudo rm -r /usr/local/bin/zeta
else
    echo "zeta entry point was not found"
fi
