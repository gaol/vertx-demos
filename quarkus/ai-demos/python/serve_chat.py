from flask import Flask, request, jsonify
import torch
from transformers import pipeline
import os

app = Flask(__name__)
http_port = os.environ.get('HTTP_PORT', 5000)
model_root_path = os.environ.get('LLM_ROOT_PATH', "c:\\ai\\models")
# load the llama3 chat model
llama3_path = f"{model_root_path}\\huggingface\\meta-llama"
llama3_pipeline = pipeline("text-generation", model=llama3_path, model_kwargs={"torch_dtype": torch.bfloat16}, device="cuda")

@app.route('/chat/completions', methods=['POST'])
def generate_text():
    try:
        # Get the input data from the request
        data = request.get_json()
        print(f"The whole request: {data}")
        input_text = data.get('messages', '')
        print(f"Going to generate answers for prompt: {input_text}")
        output_text = llama3_pipeline(input_text)
        print(f"Answer: {output_text}")
        return jsonify({'choices': [{"text": output_text}]})

    except Exception as e:
        # Handle any exceptions that occur and return an error message
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=http_port)
