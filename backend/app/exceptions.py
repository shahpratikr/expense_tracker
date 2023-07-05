class InvalidPassword(Exception):
    def __init__(self, error: str):
        self.error = error

    def __str__(self):
        return repr(self.error)


class UsernameAlreadyTaken(Exception):
    def __init__(self, error: str):
        self.error = error

    def __str__(self):
        return repr(self.error)
