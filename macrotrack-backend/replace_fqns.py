import os

directory = 'src/main/java'

for root, _, files in os.walk(directory):
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r') as f:
                content = f.read()
            
            # Replace FQNs
            new_content = content.replace('com.nutritrack.util.MetabolicConstants.', 'MetabolicConstants.')
            new_content = new_content.replace('@org.springframework.data.repository.query.Param', '@Param')
            
            # Add Param import if missing and @Param used
            if '@Param' in new_content and 'import org.springframework.data.repository.query.Param;' not in new_content:
                new_content = new_content.replace('import org.springframework.stereotype.Repository;', 'import org.springframework.stereotype.Repository;\nimport org.springframework.data.repository.query.Param;')
                new_content = new_content.replace('import org.springframework.data.jpa.repository.JpaRepository;', 'import org.springframework.data.jpa.repository.JpaRepository;\nimport org.springframework.data.repository.query.Param;')

            if new_content != content:
                with open(filepath, 'w') as f:
                    f.write(new_content)

print("FQNs replaced.")
