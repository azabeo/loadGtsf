#!/bin/bash
# Proper header for a Bash script.

# dumps db into bkp folder

DATE='date '''+%Y%m%d.%H%M''''
FILE='/Users/azabeo/NetBeansProjects/loadGtsf/db_bkp/ipmobman'$($DATE)'.sql'

echo $($FILE)

#echo '/Applications/MAMP/Library/bin/mysqldump -u root -proot ipmobman > '$($FILE)
#echo 'zip -r /Users/azabeo/NetBeansProjects/loadGtsf/db_bkp/ipmobman'$($DATE)'.sql.zip /Users/azabeo/NetBeansProjects/loadGtsf/db_bkp/ipmobman'$($DATE)'.sql'