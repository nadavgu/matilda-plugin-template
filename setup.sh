# Usage: ./setup.sh [clean|install|dist]

set -e

# Paths (adjust if needed)
GRADLE_DIR="./agent-plugin"   # Change this to the actual directory name
PYTHON_DIR="."

TEMPLATE_DIR="./template"   # Change this to the actual directory name
PROTO_DIR="$TEMPLATE_DIR/protos"
GENERATED_DIR="$TEMPLATE_DIR/generated"
RESOURCES_DIR="$TEMPLATE_DIR/resources"

do_clean() {
  echo "Cleaning Gradle project..."
  (cd "$GRADLE_DIR" && ./gradlew clean)

  echo "Cleaning Python project..."
  find "$PYTHON_DIR" -type d -name "__pycache__" -exec rm -rf {} +
  rm -rf build dist *.egg-info

  echo "Deleting 'generated' directory..."
  rm -rf "$GENERATED_DIR"

  echo "Deleting *.pb2.py files in $PROTO_DIR..."
  find "$PROTO_DIR" -name "*_pb2.py" -type f -delete

  echo "Cleaning resources directory except .gitignore and __init__.py..."
  find "$RESOURCES_DIR" -type f ! -name ".gitignore" ! -name "__init__.py" -delete

  echo "Clean completed."
}

do_install() {
  echo "Assembling Gradle project..."
  (cd "$GRADLE_DIR" && ./gradlew assemble)

  echo "Installing Python project..."
  pip install .

  echo "Install completed."
}

do_dist() {
  echo "Assembling Gradle project..."
  (cd "$GRADLE_DIR" && ./gradlew assemble)

  echo "Creating Python distribution..."
  python setup.py sdist bdist_wheel

  echo "Distribution created."
}

# Main loop over all arguments
if [ "$#" -eq 0 ]; then
  echo "Usage: $0 [clean] [install] [dist]"
  exit 1
fi

for action in "$@"; do
  case "$action" in
    clean)
      do_clean
      ;;
    install)
      do_install
      ;;
    dist)
      do_dist
      ;;
    *)
      echo "Unknown action: $action"
      echo "Usage: $0 [clean] [install] [dist]"
      exit 1
      ;;
  esac
done