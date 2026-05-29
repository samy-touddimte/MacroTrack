import os

def process_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()
    
    modified = False
    if "com.macrotrack.service.analytics.WeightForecastResult" in content:
        content = content.replace("com.macrotrack.service.analytics.WeightForecastResult", "com.macrotrack.service.analytics.forecast.WeightForecastResult")
        modified = True
            
    if modified:
        with open(filepath, 'w') as f:
            f.write(content)

for root, _, files in os.walk('src/main/java'):
    for file in files:
        if file.endswith('.java'):
            process_file(os.path.join(root, file))
