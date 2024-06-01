## An OpenAI compatible image generation API

In the image generation example, we use `runwayml/stable-diffusion-v1-5`.

We don't need to clone all model files from hugging face, we will use:
* `variant=fp16`
* `use_safetensors=True`
* `safety_checker=None`

So we only need to download the following files:

```bash
[🎩 lgao@lins-p1 stable-diffusion-v1-5]$ tree
.
├── feature_extractor
│   └── preprocessor_config.json
├── model_index.json
├── safety_checker
│   ├── config.json
├── scheduler
│   └── scheduler_config.json
├── text_encoder
│   ├── config.json
│   ├── model.fp16.safetensors
├── tokenizer
│   ├── merges.txt
│   ├── special_tokens_map.json
│   ├── tokenizer_config.json
│   └── vocab.json
├── unet
│   ├── config.json
│   ├── diffusion_pytorch_model.fp16.safetensors
└── vae
    ├── config.json
    ├── diffusion_pytorch_model.fp16.safetensors
```

