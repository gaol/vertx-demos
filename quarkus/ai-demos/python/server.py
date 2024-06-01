from flask import Flask, request, jsonify
from diffusers import StableDiffusionPipeline
from torch import autocast
import torch
from transformers import pipeline
from io import BytesIO
from PIL import Image
import base64
import os

app = Flask(__name__)
http_port = os.environ.get('HTTP_PORT', 5000)

model_root_path = os.environ.get('LLM_ROOT_PATH', "c:\\ai\\models")

# Load the Stable Diffusion model
diffusion_path = f"{model_root_path}\\huggingface\\stable-diffusion-v1-5"  # Path to the downloaded model
diffusion_pipe = StableDiffusionPipeline.from_pretrained(diffusion_path, variant="fp16", torch_dtype=torch.float16, use_safetensors=True, safety_checker=None)
diffusion_pipe = diffusion_pipe.to("cuda")  # Use GPU for faster inference

# load the llama3 chat model
llama3_path = f"{model_root_path}\\huggingface\\meta-llama"
llama3_pipeline = pipeline("text-generation", model=llama3_path, model_kwargs={"torch_dtype": torch.bfloat16}, device="cuda")


@app.route('/images/generations', methods=['POST'])
def generate_image():
    try:
        # Get the input data from the request
        data = request.get_json()
        input_text = data.get('prompt', '')
        n = data.get('n', '1')
        steps = data.get('steps', 50)

        print(f"Going to generate {n} image(s) from prompt: {input_text}")
        with autocast("cuda"):
            images = diffusion_pipe(input_text, num_inference_steps=steps, num_images_per_prompt=n).images

        print("Images generated.")
        # Save images to BytesIO objects
        image_files = []
        for image in images:
            image_bytes = BytesIO()
            image.save(image_bytes, format="PNG")
            image_bytes.seek(0)
            image_files.append(image_bytes)

        print("Images files composed.")
        # Encode images as base64 strings
        encoded_images = []
        for image_bytes in image_files:
            encoded_image = base64.b64encode(image_bytes.getvalue()).decode('utf-8')
            encoded_images.append({"b64_json": encoded_image})

        print("Images results ready.")
        return jsonify({'data': encoded_images})

    except Exception as e:
        # Handle any exceptions that occur and return an error message
        return jsonify({'error': str(e)}), 500


@app.route('/completions', methods=['POST'])
def generate_text():
    try:
        # Get the input data from the request
        data = request.get_json()
        print(f"The whole request: {data}")
        input_text = data.get('prompt', '')
        print(f"Going to generate answers for prompt: {input_text}")
        output_text = llama3_pipeline(input_text)
        print(f"Answer: {output_text}")
        return jsonify({'choices': [{"text": output_text}]})

    except Exception as e:
        # Handle any exceptions that occur and return an error message
        return jsonify({'error': str(e)}), 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=http_port)
