import sys

def sum(v1):
    result = "SurveyDocument is " + v1
    print(result)


def main(argv):
    sum(argv[1])

if __name__ == "__main__":
    main(sys.argv)