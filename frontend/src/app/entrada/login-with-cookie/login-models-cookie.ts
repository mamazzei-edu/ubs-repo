export interface LoginResponseCookie {
    token: string;
    userId: string;
    expiresIn: number;
    createdAt: number;
    refreshToken: string;
    roles: string[];
}

