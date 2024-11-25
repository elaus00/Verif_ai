import os
from pathlib import Path
from datetime import datetime

def get_directory_structure(root_path: str, exclude_dirs: list = None, exclude_files: list = None, indent: str = ""):
    """
    폴더 구조를 문자열 리스트로 반환하는 함수
    
    Parameters:
    root_path (str): 구조를 보고 싶은 루트 폴더 경로
    exclude_dirs (list): 제외할 디렉토리 목록 (기본값: None)
    exclude_files (list): 제외할 파일 목록 (기본값: None)
    indent (str): 들여쓰기 문자열 (재귀 호출용)
    """
    if exclude_dirs is None:
        exclude_dirs = ['.git', '__pycache__', 'node_modules', 'venv', 'build']
    if exclude_files is None:
        exclude_files = ['.DS_Store', '.gitignore']
        
    root = Path(root_path)
    
    def should_exclude(path):
        name = path.name
        return (path.is_dir() and name in exclude_dirs) or \
               (path.is_file() and name in exclude_files) or \
               name.startswith('.')
    
    # 디렉토리 내 모든 항목을 가져와서 정렬
    items = sorted(root.iterdir(), key=lambda x: (x.is_file(), x.name.lower()))
    
    structure = []
    for idx, path in enumerate(items):
        if should_exclude(path):
            continue
            
        is_last = idx == len(items) - 1
        prefix = "└── " if is_last else "├── "
        next_indent = indent + ("    " if is_last else "│   ")
        
        if path.is_file():
            structure.append(f"{indent}{prefix}{path.name}")
        else:
            structure.append(f"{indent}{prefix}{path.name}/")
            # 재귀적으로 하위 디렉토리 탐색
            sub_structure = get_directory_structure(
                str(path), 
                exclude_dirs, 
                exclude_files, 
                next_indent
            )
            structure.extend(sub_structure)
            
    return structure

def export_tree(path: str, output_format: str = "md", exclude_dirs: list = None, exclude_files: list = None):
    """
    폴더 구조를 파일로 내보내는 함수
    
    Parameters:
    path (str): 구조를 보고 싶은 폴더 경로
    output_format (str): 출력 파일 형식 ("md" 또는 "txt")
    exclude_dirs (list): 제외할 디렉토리 목록
    exclude_files (list): 제외할 파일 목록
    """
    try:
        abs_path = os.path.abspath(path)
        structure = get_directory_structure(path, exclude_dirs, exclude_files)
        
        # 현재 시간을 파일명에 포함
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        output_filename = f"folder_structure_{timestamp}.{output_format}"
        
        with open(output_filename, 'w', encoding='utf-8') as f:
            if output_format == "md":
                f.write(f"# Folder Structure - {abs_path}\n")
                f.write("```\n")
                f.write("\n".join(structure))
                f.write("\n```\n")
            else:  # txt
                f.write(f"Folder Structure - {abs_path}\n")
                f.write("=" * 50 + "\n")
                f.write("\n".join(structure))
        
        print(f"폴더 구조가 '{output_filename}' 파일로 내보내기 되었습니다.")
        
    except Exception as e:
        print(f"Error occurred: {str(e)}")

# 사용 예시
if __name__ == "__main__":
    try:
        # 현재 디렉토리 구조를 마크다운 파일로 내보내기
        export_tree(".", "md")
        
        # 텍스트 파일로 내보내기하려면:
        # export_tree(".", "txt")
        
        # 특정 폴더/파일 제외하여 내보내기
        # export_tree(".", "md", 
        #            exclude_dirs=['venv', 'dist'], 
        #            exclude_files=['.gitignore'])
    except Exception as e:
        print(f"Error occurred: {str(e)}")