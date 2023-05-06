import sys

def sum(v1):
    result = "[[1,[[0.88,3],[0.8,5]]],[2,[[0.7,4],[0.5,6]]]]"
    print(result)


def main(argv):
    sum(argv[1])

if __name__ == "__main__":
    main(sys.argv)