# prompt: i want to create python script that will use open source ai model to translate the android string.xml file for other languages...

# Run: pip install lxml transformers torch

import xml.etree.ElementTree as ET
from transformers import MarianMTModel, MarianTokenizer
import os

# Function to translate a single string
def translate_string(text, source_lang, target_lang, model, tokenizer):
    if not text:
        return ""
    try:
        # Some models require a specific prefix for translation tasks
        # This may vary depending on the model used.
        # For MarianMT models, the target language code might be needed as a prefix.
        # Check the model's documentation for the correct format.
        # For example, for English to French, it might be f">>{target_lang}<< {text}"
        # However, a more general approach is to just pass the text.
        inputs = tokenizer(text, return_tensors="pt", padding=True, truncation=True, max_length=512)
        translated_tokens = model.generate(**inputs, max_length=512, num_beams=4, early_stopping=True)
        translated_text = tokenizer.decode(translated_tokens[0], skip_special_tokens=True)
        return translated_text
    except Exception as e:
        print(f"Error translating text: '{text}' - {e}")
        return text # Return original text if translation fails

# Function to translate an XML string file
def translate_strings_xml(input_file_path, output_dir, source_lang, target_lang):
    try:
        tree = ET.parse(input_file_path)
        root = tree.getroot()

        # Load the translation model and tokenizer
        # You need to choose a suitable pre-trained model.
        # Examples: 'Helsinki-NLP/opus-mt-en-fr' (English to French), 'Helsinki-NLP/opus-mt-es-en' (Spanish to English)
        # Find models for your language pair on the Hugging Face Model Hub: https://huggingface.co/models?library=transformers&sort=downloads&search=marianmt
        model_name = f"Helsinki-NLP/opus-mt-{source_lang}-{target_lang}"
        print(f"Loading translation model: {model_name}")
        tokenizer = MarianTokenizer.from_pretrained(model_name)
        model = MarianMTModel.from_pretrained(model_name)
        print("Model loaded successfully.")

        # Create the output directory if it doesn't exist
        os.makedirs(output_dir, exist_ok=True)
        output_file_path = os.path.join(output_dir, os.path.basename(input_file_path))

        # Translate each string element
        for string_elem in root.findall('string'):
            original_text = string_elem.text
            if original_text is not None:
                translated_text = translate_string(original_text, source_lang, target_lang, model, tokenizer)
                string_elem.text = translated_text
                print(f"Translated: '{original_text}' -> '{translated_text}'")
            # Handle CDATA sections if they exist (less common in standard strings.xml but good practice)
            # This would require more complex parsing and handling. For simplicity, assuming standard text nodes.


        # Save the translated XML to a new file
        tree.write(output_file_path, encoding='utf-8', xml_declaration=True)
        print(f"Translated file saved to: {output_file_path}")

    except FileNotFoundError:
        print(f"Error: Input file not found at {input_file_path}")
    except ET.ParseError:
        print(f"Error: Could not parse XML file at {input_file_path}. Make sure it's valid XML.")
    except Exception as e:
        print(f"An error occurred during translation: {e}")

# --- Example Usage ---

# Create a dummy strings.xml file for demonstration
dummy_xml_content = """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">My Application</string>
    <string name="welcome_message">Welcome to our app!</string>
    <string name="button_text">Click Me</string>
    <string name="simple_text">This is a simple text string.</string>
</resources>
"""

# Write the dummy content to a file
input_file = "strings.xml"
with open(input_file, "w", encoding="utf-8") as f:
    f.write(dummy_xml_content)

print(f"Created dummy input file: {input_file}")

# Define input and output paths, source and target languages
input_strings_file = input_file  # Or replace with the path to your actual strings.xml
output_directory = "translated_values_fr" # Directory for the translated strings (e.g., values-fr for French)
source_language = "en" # Source language code (e.g., 'en' for English)
target_language = "fr" # Target language code (e.g., 'fr' for French)

# IMPORTANT: Ensure the model exists for your source and target language pair.
# Check the Hugging Face Model Hub for available 'Helsinki-NLP/opus-mt' models.

# Translate the strings.xml file
translate_strings_xml(input_strings_file, output_directory, source_language, target_language)