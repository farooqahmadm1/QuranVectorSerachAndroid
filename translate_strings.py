# prompt: i want to create python script that will use open source ai model to translate the android string.xml file for other languages...
# # i have already strings.xml file in values folder for ur(urdu) and ar(arabic) lanauges.. i want to translate it
!pip install lxml transformers torch


import xml.etree.ElementTree as ET
from transformers import MarianMTModel, MarianTokenizer
import os

def parse_strings_xml(filepath):
    """Parses a strings.xml file and extracts string key-value pairs."""
    tree = ET.parse(filepath)
    root = tree.getroot()
    strings = {}
    for string_elem in root.findall('string'):
        name = string_elem.get('name')
        value = string_elem.text
        if name and value is not None:
            strings[name] = value.strip()
    return strings

def translate_string(text, model, tokenizer, src_lang="en", target_lang="ur"):
    """Translates a single string using the provided model."""
    # Prepare the text for the model
    # The format '>>target_lang<< text' is common for multi-lingual models like MarianMT
    input_text = f">>{target_lang}<< {text}"

    # Tokenize the input
    input_ids = tokenizer(input_text, return_tensors="pt", padding=True).input_ids

    # Generate translation
    translated_ids = model.generate(input_ids, max_length=512)

    # Decode the output
    translated_text = tokenizer.decode(translated_ids[0], skip_special_tokens=True)

    return translated_text

def create_translated_xml(translated_strings, output_filepath):
    """Creates a new strings.xml file with translated strings."""
    root = ET.Element("resources")
    for name, value in translated_strings.items():
        string_elem = ET.SubElement(root, "string", name=name)
        string_elem.text = value # You might need to handle escaping XML special characters

    tree = ET.ElementTree(root)
    tree.write(output_filepath, encoding="utf-8", xml_declaration=True)

# --- Main Script Logic ---

# Define file paths and languages
base_strings_file = '/content/strings.xml' # Your base strings.xml file (e.g., English)
target_languages = ['ur', 'ar'] # Target languages

# Load the base strings
base_strings = parse_strings_xml(base_strings_file)

# Choose and load an open-source model and tokenizer
# Example using Hugging Face and a MarianMT model for English to Urdu
# You'll need to find appropriate models for other language pairs if needed
model_name_en_ur = 'Helsinki-NLP/opus-mt-en-ur'
tokenizer_en_ur = MarianTokenizer.from_pretrained(model_name_en_ur)
model_en_ur = MarianMTModel.from_pretrained(model_name_en_ur)

# Example using Hugging Face and a MarianMT model for English to Arabic
model_name_en_ar = 'Helsinki-NLP/opus-mt-en-ar'
tokenizer_en_ar = MarianTokenizer.from_pretrained(model_name_en_ar)
model_en_ar = MarianMTModel.from_pretrained(model_name_en_ar)


# Mapping of language codes to models and tokenizers
language_models = {
    'ur': (model_en_ur, tokenizer_en_ur),
    'ar': (model_en_ar, tokenizer_en_ar)
    # Add more languages and their models/tokenizers here
}


for lang in target_languages:
    print(f"Translating to {lang}...")
    translated_strings = {}
    model, tokenizer = language_models.get(lang)

    if model and tokenizer:
        for name, original_value in base_strings.items():
            # Basic handling for potential placeholders - may need more robust logic
            # This simple approach just translates the whole string
            translated_value = translate_string(original_value, model, tokenizer, target_lang=lang)
            translated_strings[name] = translated_value
            print(f"  '{original_value}' -> '{translated_value}'") # Print progress

        # Define the output directory and file
        output_dir = f'values-{lang}'
        os.makedirs(output_dir, exist_ok=True) # Create directory if it doesn't exist
        output_file = os.path.join(output_dir, 'strings.xml')

        # Create the translated XML file
        create_translated_xml(translated_strings, output_file)
        print(f"Created {output_file}")
    else:
        print(f"No model found for language: {lang}. Skipping.")

print("Translation process finished.")
