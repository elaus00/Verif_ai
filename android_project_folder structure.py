import os
import json

def extract_android_project_structure(project_path):
    """
    안드로이드 프로젝트 구조를 추출하는 함수
    
    Args:
        project_path (str): 안드로이드 프로젝트 루트 경로
    
    Returns:
        dict: 프로젝트 구조를 담은 딕셔너리
    """
    
    # 무시할 디렉토리/파일 목록
    IGNORE_DIRS = {'.gradle', '.idea', 'build', '.git'}
    IGNORE_FILES = {'.DS_Store', 'local.properties'}
    
    def scan_directory(dir_path):
        structure = {
            'type': 'directory',
            'name': os.path.basename(dir_path),
            'contents': []
        }
        
        try:
            items = os.listdir(dir_path)
            
            # 디렉토리와 파일을 분리하여 정렬
            dirs = []
            files = []
            
            for item in items:
                if item in IGNORE_FILES:
                    continue
                    
                full_path = os.path.join(dir_path, item)
                
                # 무시할 디렉토리 체크
                if os.path.isdir(full_path):
                    if item in IGNORE_DIRS:
                        continue
                    dirs.append(item)
                else:
                    files.append(item)
            
            # 정렬된 순서로 처리
            for d in sorted(dirs):
                full_path = os.path.join(dir_path, d)
                structure['contents'].append(scan_directory(full_path))
                
            for f in sorted(files):
                structure['contents'].append({
                    'type': 'file',
                    'name': f
                })
                
        except Exception as e:
            print(f"Error scanning {dir_path}: {str(e)}")
            
        return structure

    def save_structure(structure, output_file):
        """결과를 JSON 파일로 저장"""
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(structure, f, indent=2, ensure_ascii=False)

    # 메인 실행
    result = scan_directory(project_path)
    
    # JSON 파일로 저장
    output_path = os.path.join(project_path, 'project_structure.json')
    save_structure(result, output_path)
    
    return result

# 사용 예시
if __name__ == "__main__":
    project_path = "C:/Users/elaus/AndroidStudioProjects/Verif_ai"
    structure = extract_android_project_structure(project_path)