from sys import argv
from functions.tools import run_sorsim

# Read the data from the inputs of the user
if __name__ == "__main__":
    if len(argv) != 6:
        print("Usage: python run_sorsim.py <jar_file_location> <pathin> <pathout> <code_klassen> <verbose>")
        print("Example: python run_sorsim.py /path/to/jar/file.jar /path/to/input/fname_in.csv /path/to/output/fname_out.csv 6 True")
        print("Using default values")
        jar_file_location = None
        pathin = None
        pathout = None
        code_klassen = None
    else:
        jar_file_location = argv[1]
        pathin = argv[2]
        pathout = argv[3]
        code_klassen = argv[4]
        verbose = argv[5]
        if verbose == "True":
            verbose = True
        else:
            verbose = False
            
    assortment = run_sorsim(jar_file_location, pathin, pathout, code_klassen, verbose=verbose)