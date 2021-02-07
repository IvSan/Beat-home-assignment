# Beat-home-assignment

## Implementation

### Assumptions

Based on technical requirement
> Assume we will try to pipe several GBs worth of samples to your script.

My assumption would be an idea, that runtime environment is capable to keep in memory all **rides** (ride id, total cost and
last position), which number will be several orders of magnitude fewer than number of samples in the input dataset.  
So the solution do not require interim result writes to file.

Also, all ride cost calculations will be processed in a timezone of first occurred position.

### Prepare data
The implementation does not require the input dataset to contain continuous row blocks for each individual ride. Different rides can be mixed up.
However, strictly ascending timestamp order within one ride is required.

Please sort the input dataset by a timestamp, that is crucial for a proper program functioning.  
The best way to do it is use of `sort` form [GNU coreutils](https://www.gnu.org/software/coreutils/).  
Software is often preinstalled on Linux systems. Also, it's available for [MacOs](https://formulae.brew.sh/formula/coreutils).  
Use the command: `gsort -n -t , -k 4 paths.csv > time_sorted_paths.csv`