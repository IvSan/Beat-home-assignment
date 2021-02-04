# Beat-home-assignment

Firstly please sort the input dataset by a timestamp, that is crucial for a proper program functioning.  
The Best way is to use `sort` form [GNU coreutils](https://www.gnu.org/software/coreutils/).  
Software is often preinstalled on Linux systems. Also, it's available for [MacOs](https://formulae.brew.sh/formula/coreutils).  

Use the command:  
`gsort -n -t , -k 4 paths.csv > time_sorted_paths.csv`