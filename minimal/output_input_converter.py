import pandas as pd
from sys import argv
from functions.tools import output_input_converter

# Read the data from the inputs of the user
if __name__ == "__main__":
    if len(argv) != 6:
        print("Usage: python output_input_converter.py <folder_in> <file_in_name> <folder_out> <file_out_name> <dead_cohorts_true_or_False>")
        print("Example: python output_input_converter.py /path/to/input/ fname_in.csv /path/to/output/ fname_out.csv True")
        assert False
    else:
        folder_in = argv[1]
        file_in_name = argv[2]
        folder_out = argv[3]
        file_out_name = argv[4]
        data_in = pd.read_csv(f'{folder_in + file_in_name}')
        # Convert the data to 
        dead_cohorts = argv[5]
        if dead_cohorts == "True":
            dead_cohorts = True
        else:
            dead_cohorts = False
        data_out = output_input_converter(folder_in, data_in, dead_cohorts=dead_cohorts)
        # Save the data to a new file
        data_out.to_csv(f'{folder_out + file_out_name}', index=False , sep=';')