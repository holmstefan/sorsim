This folder contains the mininal information and data to run the output of the ForClim model as input for SorClim.

**The important files are only three:**
output_input_converter.py -> convert output of ForClim in possible inputs for SorSim
run_sorsim.py -> run sorsim from terminal (useful when we will have to deal with a lot of simulations results)
functions/tools.py -> a set of functions to make the desired operation requested by the scripts

** The input files are:**
- file created by ForClim
-- for example, we have deadCohorts_sample.csv in the folder ./testdata/inputs/
- SortimentsVorgabenListe.csv taken from SorSim

The outputs created are in the folder that are decided by the user (default is folder where the call occurs.)
From output_input_converter:
deadCohorts_sample.csv -> a tree list created by converting the ForClim data as an input for ForClim
From run_sorsim:
Output.csv -> the assortment


# Example to run the code
```
python run_sorsim.py ../SorSim4Python.jar testdata/inputs/Baumliste.csv testdata/outputs/Output.csv 6 True
python output_input_converter.py testdata/inputs/ deadCohorts_sample.csv testdata/intermediate/ deadCohorts_sample.csv True
python run_sorsim.py ../SorSim4Python.jar testdata/intermediate/deadCohorts_sample.csv testdata/outputs/deadCohorts_sample.csv 6 True
```
# Suggestions 
To check the correct functioning, you can create the file using the GUI version of SorSim and then run a diff.

#Notes 
Java needs to be installed to run SorSim

# Python requirements
Packages need to be installed are:
-. pandas
-. py4j

TODO create a requirements file