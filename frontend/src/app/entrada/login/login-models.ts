export interface LoginResponse {
    token: string;
    userId: string;
    expiresIn: number;
    createdAt: number;
    roles: string[];
}

