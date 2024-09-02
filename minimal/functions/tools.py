import pandas as pd
from py4j.java_gateway import JavaGateway 

#Information taken from the tool provided by WSL
baumart2code = {"Fichte":100,
"Tanne":101,
"Foehre":102,
"Laerche":103,
"Ubrige Nadelholzer":199,
"Buche":200,
"Eiche":201,
"Esche":202,
"Ahorn":203,
"Ubrige Laubholzer":299,
"Ubrige":-1}

# Define a function to map Latin species names to German names
def map_species(latin_species):
    species_mapping = {}

    for species in latin_species:
        if "Pinus" in species:
            species_mapping[species] = "Foehre"  
        elif "Picea abies" in species:
            species_mapping[species] = "Fichte"  
        elif "Abies alba" in species:
            species_mapping[species] = "Tanne"
        elif "Larix decidua" in species:
            species_mapping[species] = "Laerche"
        elif "Fagus sylvatica" in species:
            species_mapping[species] = "Buche"
        elif "Quercus" in species:
            species_mapping[species] = "Eiche"
        elif "Fraxinus excelsior" in species:
            species_mapping[species] = "Esche"
        elif "Acer" in species:
            species_mapping[species] = "Ahorn"
        elif "Pseudotsuga menziesii" in species:
            species_mapping[species] = "Ubrige Nadelholzer"
        elif "Taxus" in species: #Taxus baccata
            species_mapping[species] = "Ubrige Nadelholzer"
        elif "Thuja" in species: #Thuja occidentalis
            species_mapping[species] = "Ubrige Nadelholzer"
        elif "Acer" in species: #Acer pseudoplatanus, Acer campestre, Acer platanoides
            species_mapping[species] = "Ubrige Laubholzer"
        elif "Alnus" in species: #Alnus glutinosa, Alnus incana, Alnus viridis
            species_mapping[species] = "Ubrige Laubholzer"
        elif "Betula" in species: #Betula pendula, Betula pubescens
            species_mapping[species] = "Ubrige Laubholzer"
        elif "Carpinus" in species: #Carpinus betulus
            species_mapping[species] = "Ubrige Laubholzer"
        elif "Castanea" in species: #Castanea sativa
            species_mapping[species] = "Ubrige Laubholzer"
        elif "Corylus" in species: #Corylus avellana 
            species_mapping[species] = "Ubrige Laubholzer"
        elif "Populus" in species: #Populus nigra, Populus tremula
            species_mapping[species] = "Ubrige Laubholzer"
        elif "Salix" in species: #Salix Alba
            species_mapping[species] = "Ubrige Laubholzer"
        elif "Sorbus" in species: #Sorbus aria, Sorbus aucuparia
            species_mapping[species] = "Ubrige Laubholzer"
        elif "Tilia" in species: #Tilia cordata, Tilia platyphyllos
            species_mapping[species] = "Ubrige Laubholzer"
        elif "Ulmus glabra" in species:
            species_mapping[species] = "Ubrige Laubholzer"
        # If none of the specific conditions match, you can label it as "Ubrige" (Other)
        else:
            species_mapping[species] = "Ubrige" 
    return species_mapping


def import_classification_of_trees(folder_inputs):
    """
    This function imports the classification of trees from the ForClim tool and maps the species names to the Baumart-Code of the input data for SorClim.

    Parameters:
    folder_inputs (str): The path to the folder containing the input data about the species.
    
    Returns:
    id_to_species_ForClim (dict): A dictionary from the speciesid to the latin name of the species from the ForClim tool.
    ForClim_species_to_Baumart_Code (dict): A dictionary from the species names from the ForClim output to the Baumart-Code of the input data for SorClim.
    species_mapping (dict): A dictionary from the latin species names to the Baumart-Code.
    """        
    # we take the labelling of the species of the ForClim output
    df_species_and_ids = pd.read_csv(folder_inputs +'templateSpec_V2.txt', sep='\t', index_col=0)
    # we create a list of the unique species names
    latin_species = df_species_and_ids.index.tolist()
    # we create a mapping from the latin species names to the Baumart-Code
    species_mapping = map_species(latin_species)
    # with this mapping, we can now create a dictionary from the species names from the ForClim output to the Baumart-Code of the input data for SorClim
    ForClim_species_to_Baumart_Code = {species: baumart2code[species_mapping[species]] for species in species_mapping.keys()}
    # to use this list, we have to add the "FullLatinNAme" column to the dataframe
    # So first we create dictionary from the speciesid to lating name from the ForClim
    id_to_species_ForClim = {row['speciesID']: index for index, row in df_species_and_ids.iterrows()}
    return id_to_species_ForClim, ForClim_species_to_Baumart_Code, species_mapping

def output_input_converter(folder_inputs, data_out_ForClim, dead_cohorts = False):
    """
    This function takes the output of the ForClim tool and converts it to the input format of the SorClim tool.

    Parameters:
    folder_inputs (str): The path to the folder containing the input data about the species.
    data_out_ForClim (pd.DataFrame): The output of the ForClim tool.

    Returns:
    data_out_ForClim_as_in_SorSim (pd.DataFrame): The output of the ForClim tool in the input format of the SorClim tool.
    """
    # we keep only the columns that are needed for the input of SorClim
    if dead_cohorts == False:
        trees_number = "trees"
    elif dead_cohorts == True:
        trees_number = "dtrees"
    columns = ['id', 'year', 'height', 'diameter', 'species', 'speciesid', 'cohortID', trees_number, 'run']
    data_out_ForClim_as_in_SorSim = data_out_ForClim[columns].copy()
    del data_out_ForClim
    # we import the classification of trees from the ForClim tool and map the species names to the Baumart-Code of the input data for SorClim
    id_to_species_ForClim, ForClim_species_to_Baumart_Code, species_mapping = import_classification_of_trees(folder_inputs)
    # then we add a column to the dataframe with the latin name
    data_out_ForClim_as_in_SorSim['FullLatinName'] = data_out_ForClim_as_in_SorSim['speciesid'].map(id_to_species_ForClim)
    # add column Baumart-Code to the data_out_ForClim_as_in_SorSim
    data_out_ForClim_as_in_SorSim['Baumart-Code'] = data_out_ForClim_as_in_SorSim['FullLatinName'].map(ForClim_species_to_Baumart_Code)
    # add colum Baumart to the data_out_ForClim_as_in_SorSim
    data_out_ForClim_as_in_SorSim['Baumart'] = data_out_ForClim_as_in_SorSim['FullLatinName'].map(species_mapping)
    data_out_ForClim_as_in_SorSim.tail()
    # we add one emptycolumn D7m
    data_out_ForClim_as_in_SorSim['D7m'] = 0
    # we change the name of the "run" column to "Beschrieb"
    data_out_ForClim_as_in_SorSim = data_out_ForClim_as_in_SorSim.rename(columns={'run': 'Beschrieb'})
    # transform the height from cm to m
    data_out_ForClim_as_in_SorSim['height'] = data_out_ForClim_as_in_SorSim['height'] / 100
    # we now duplicate rows given the number of trees in the column trees
    data_out_ForClim_as_in_SorSim = data_out_ForClim_as_in_SorSim.loc[data_out_ForClim_as_in_SorSim.index.repeat(data_out_ForClim_as_in_SorSim[trees_number])]
    # we drop the columns that are not needed
    data_out_ForClim_as_in_SorSim = data_out_ForClim_as_in_SorSim.drop(['id', 'cohortID', 'speciesid', 'species', trees_number, 'FullLatinName'], axis=1)
    # we reorder the columns
    data_out_ForClim_as_in_SorSim = data_out_ForClim_as_in_SorSim[['Baumart', 'Baumart-Code', 'Beschrieb', 'year', 'diameter', 'D7m', 'height']]
    # we rename the columns using the same names as in the input data for SorClim
    data_out_ForClim_as_in_SorSim.columns = ['Baumart', 'Baumart-Code', 'Beschrieb', 'Aufnahme-Datum', 'BHD', 'D7m', 'SchaftLaenge_m']
    # drop the row that have -1.0 or NaN as Baumart-Code
    data_out_ForClim_as_in_SorSim = data_out_ForClim_as_in_SorSim[data_out_ForClim_as_in_SorSim['Baumart-Code'] != -1.0]
    data_out_ForClim_as_in_SorSim = data_out_ForClim_as_in_SorSim.dropna(subset=['Baumart-Code'])
    # convert the baumart-code to integer
    data_out_ForClim_as_in_SorSim['Baumart-Code'] = data_out_ForClim_as_in_SorSim['Baumart-Code'].astype(int)
    # reset the index of the dataframe
    data_out_ForClim_as_in_SorSim = data_out_ForClim_as_in_SorSim.reset_index(drop=True)
    # Create a new column "#ID" using the index of the dataframe
    data_out_ForClim_as_in_SorSim['#ID'] = data_out_ForClim_as_in_SorSim.index
    #make this the first column
    data_out_ForClim_as_in_SorSim = data_out_ForClim_as_in_SorSim[['#ID', 'Baumart', 'Baumart-Code', 'Beschrieb', 'Aufnahme-Datum', 'BHD', 'D7m', 'SchaftLaenge_m']]
    # we save the data to a new file
    return data_out_ForClim_as_in_SorSim

def run_sorsim(jar_file_location=None, pathin=None, pathout=None, code_klassen=None, verbose=False):
    """
    This function runs the SorSim tool and saves the output to a file called Output.csv in the current folder.

    Args:
    jar_file_location (str): The path to the jar file of the SorSim tool.
    pathin (str): The path to the input file.
    pathout (str): The path to the output file.
    code_klassen (int): The number of code classes for the assortment: 1 -> L1, 2 ->L2 ... 6 -> L1+L2+L3, 7->Restholz .
    """
    # Run the SorSim tool
    if jar_file_location is None:
        jar_file_location = "./sorsim/SorSim4Python.jar"
    if verbose==True:
        print("jar_file_location is:" + jar_file_location)
    gg = JavaGateway.launch_gateway(classpath=jar_file_location, die_on_exit=True)
    myclass_instance = gg.jvm.ch.wsl.sustfor.sorsim.python.SorSimEntryPoint()
    wrapper = myclass_instance.getSimpleWrapper()
    if pathin is not None:
        if verbose==True:
            print("pathin is:" + pathin)
        wrapper.setFileTreeList(pathin)
    if pathout is not None:
        if verbose==True:
            print("pathout is:" + pathout)
        wrapper.setFileOutput(pathout)
    if code_klassen is not None:
        if verbose==True:
            print("code_klassen is:" + str(code_klassen))
        wrapper.setCombinationOfLengthClasses_category(float(code_klassen))
    if verbose==True:
        print("Making assortments")
    assortment = wrapper.makeAssortments()
    return assortment